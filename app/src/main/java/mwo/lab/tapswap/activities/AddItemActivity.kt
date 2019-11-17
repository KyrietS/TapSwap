package mwo.lab.tapswap.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.RequestResult
import mwo.lab.tapswap.views.LoadingView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File



class AddItemActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY_CODE = 1
        private const val REQUEST_CAMERA_CODE = 2
        private const val REQUEST_PERMISSIONS_FOR_GALLERY = 3
        private const val FILE_PROVIDER_AUTHORITY = "mwo.lab.tapswap.fileprovider"
    }

    private var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.title = getString(R.string.add_item)

        val title = findViewById<EditText>(R.id.title)
        val description = findViewById<EditText>(R.id.description)

        title.setRawInputType(InputType.TYPE_CLASS_TEXT)
        description.setRawInputType(InputType.TYPE_CLASS_TEXT)
        title.setOnEditorActionListener { v, actionId, event -> filterNewlines(v, actionId, event) }
        description.setOnEditorActionListener { v, actionId, event -> filterNewlines(v, actionId, event) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.icon_trash -> {
                Toast.makeText(this, "TODO: discard changes", Toast.LENGTH_SHORT).show()
            }
            R.id.icon_done -> {
                sendItem()
            }
            else -> {
                return false
            }
        }
        return true
    }

    private fun sendItem() {
        val file = File(currentPhotoPath)
        val name = findViewById<TextView>(R.id.title).text.toString()
        val desc = findViewById<TextView>(R.id.description).text.toString()

        val imageBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart = MultipartBody.Part.createFormData("photo", file.name, imageBody)
        val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
        val catBody = RequestBody.create(MediaType.parse("text/plain"), "kategoria")
        val priceBody = RequestBody.create(MediaType.parse("text/plain"), "cena")

        val api = APIService.create()

        val call = api.addItem(imagePart, nameBody, descBody, catBody, priceBody)
        val loading = findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue( object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                loading.finish()
                if(response.isSuccessful && response.body()!!.success) {
                    Toast.makeText(this@AddItemActivity, "Dodano przedmiot", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddItemActivity, MyItemsActivity::class.java)
                    startActivity(intent)
                } else {
                    onFailure(call, Throwable(response.body()!!.errors.toString()))
                }
            }
            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                Log.d("omg", t.message)
                loading.finish()
                Toast.makeText(this@AddItemActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val itemImage = findViewById<ImageView>(R.id.item_image)

        if( requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK ) {
            val selectedImageUri = data?.data
            currentPhotoPath = getPath(this, selectedImageUri!!)
            itemImage.setImageURI(selectedImageUri)
        }
        if( requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK ) {
            itemImage.setImageURI( Uri.fromFile(File(currentPhotoPath)) )
        }
    }

    /**
     * Gets absolute path to the resource under given uri.
     */
    private fun getPath(context: Context, uri: Uri): String {
        var result = ""
        val projection = arrayOf( MediaStore.Images.Media.DATA )
        val cursor: Cursor? = context.contentResolver.query( uri, projection, null, null, null )
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                val index: Int = cursor.getColumnIndexOrThrow( projection[0] )
                result = cursor.getString( index )
            }
            cursor.close()
        }
        if(result.isEmpty()) {
            result = "Not found"
        }
        return result
    }

    @Suppress("UNUSED_PARAMETER")
    fun addImage(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz zdjęcie...")
        builder.setItems(arrayOf("Galeria", "Aparat")) { _, which ->
            when (which) {
                0 -> pickFromGallery()
                1 -> pickFromCamera()
            }
        }
        builder.show()
    }

    private fun pickFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS_FOR_GALLERY)
        } else {
            //Create an Intent with action as ACTION_PICK
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/jpeg"
            // Launching the Intent
            startActivityForResult(intent, REQUEST_GALLERY_CODE)
        }
    }

    /**
     * Send request to camera app for taking a photo
     */
    private fun pickFromCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if( pictureIntent.resolveActivity(packageManager) != null ) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch( e: Exception) {
                Toast.makeText(this, "Nie można utworzyć pliku", Toast.LENGTH_SHORT).show()
                return
            }
            val photoURI: Uri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(pictureIntent, REQUEST_CAMERA_CODE)
        }
    }

    /**
     * Create temp file for an image
     */
    @SuppressLint("SimpleDateFormat")
    fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File("$storageDir/item_image.jpg")
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when(requestCode) {
            REQUEST_PERMISSIONS_FOR_GALLERY -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickFromGallery()
                }
            }
        }
    }

    // Function that filters out any newline character.
    private fun filterNewlines(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        val title = findViewById<EditText>(R.id.title)
        if (event == null) {
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    // Capture soft enters in a singleLine EditText that is the last EditText
                    // This one is useful for the new list case, when there are no existing ListItems
                    title.clearFocus()
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        v.windowToken,
                        InputMethodManager.RESULT_UNCHANGED_SHOWN
                    )
                }
                EditorInfo.IME_ACTION_NEXT -> {
                    // Capture soft enters in other singleLine EditTexts
                }
                EditorInfo.IME_ACTION_GO -> {
                }
                else -> // Let the system handle all other null KeyEvents
                    return false
            }
        } else if (actionId == EditorInfo.IME_NULL) {
            // Capture most soft enters in multi-line EditTexts and all hard enters;
            // They supply a zero actionId and a valid keyEvent rather than
            // a non-zero actionId and a null event like the previous cases.
            if (event.action != KeyEvent.ACTION_DOWN) {
                return true
            }
        } else {
            // We let the system handle it when the listener is triggered by something that
            // wasn't an enter.
            return false
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        // TODO: ask if user want to save changes
        finish()
    }
}
