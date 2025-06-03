package com.example.diplomawork2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.diplomawork2.databinding.ActivityUserProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var databaseHelper: DatabaseHelper

    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = intent.getStringExtra("username") ?: ""
        binding.tvUsername.text = username
        loadProfilePhoto(username)

        databaseHelper = DatabaseHelper(this)

        val record = databaseHelper.getRecord(username)
        binding.tvRecord.text = "Record: $record"

        updateUI()

        binding.profileImage.setOnClickListener {
            if (binding.btnChangePhoto.visibility == View.GONE) {
                dispatchTakePictureIntent()
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun updateUI() {
        val drawable = binding.profileImage.drawable
        if (drawable == null || drawable.constantState == null) {
            binding.btnChangePhoto.visibility = View.GONE
        } else {
            binding.btnChangePhoto.visibility = View.VISIBLE
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
            photoFile?.also {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    it
                )
                currentPhotoPath = it.absolutePath
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir("Pictures")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotoPath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                val rotatedBitmap = rotateBitmapIfRequired(path, bitmap)
                binding.profileImage.setImageBitmap(rotatedBitmap)
                binding.btnChangePhoto.visibility = View.VISIBLE

                val username = binding.tvUsername.text.toString()
                val prefs = getSharedPreferences("user_profile", MODE_PRIVATE)
                prefs.edit().putString("profile_photo_path_$username", path).apply()
            }
        }
    }

    private fun loadProfilePhoto(username: String) {
        val prefs = getSharedPreferences("user_profile", MODE_PRIVATE)
        val path = prefs.getString("profile_photo_path_$username", null)
        if (path != null) {
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                val rotatedBitmap = rotateBitmapIfRequired(path, bitmap)
                binding.profileImage.setImageBitmap(rotatedBitmap)
                binding.btnChangePhoto.visibility = View.VISIBLE
            }
        }
    }

    // Для поворота фото по EXIF
    private fun rotateBitmapIfRequired(path: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(path)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0,
            source.width, source.height,
            matrix, true
        )
    }
}
