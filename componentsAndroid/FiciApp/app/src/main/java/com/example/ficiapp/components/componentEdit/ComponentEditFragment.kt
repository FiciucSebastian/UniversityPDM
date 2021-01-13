package com.example.ficiapp.components.componentEdit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ficiapp.BasicMapActivity
import com.example.ficiapp.EventsActivity
import com.example.ficiapp.R
import com.example.ficiapp.auth.data.AuthRepository
import com.example.ficiapp.components.data.Component
import com.example.ficiapp.core.LocationHelper
import com.example.ficiapp.core.TAG
import kotlinx.android.synthetic.main.fragment_edit_component.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ComponentEditFragment : Fragment() {
    private lateinit var viewModel: ComponentEditViewModel
    private var component: Component? = null
    private var attemptAt: Long = 0
    private val REQUEST_PERMISSION = 10
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
        initLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_edit_component, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated")
        component = arguments?.getParcelable("component")
        component?.let {
            attemptAt = Date().time
            name.setText(component?.name)
            quantity.setText(component?.quantity.toString())

            val df = SimpleDateFormat("dd.MM.yyyy")
            val date: Date = df.parse(component!!.releaseDate)
            val cal = Calendar.getInstance()
            cal.time = date

            datePicker.updateDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            inStock.isChecked = component?.inStock!!
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()

        if (!component?._id.isNullOrEmpty()) {
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.setOnClickListener {
                component?.let { it1 -> viewModel.deleteItem(it1) }
                findNavController().navigate(R.id.fragment_component_list)
            }
        }

        fab.setOnClickListener {
            Log.v(TAG, "save component")
            component?.let {
                it.name = name.text.toString()
                it.quantity = quantity.text.toString().toInt()
                it.releaseDate =
                    "${datePicker.dayOfMonth}.${datePicker.month + 1}.${datePicker.year}"
                it.inStock = inStock.isChecked
                it.attemptUpdateAt = attemptAt
                viewModel.saveOrUpdateComponent(it)
            }
        }

        takePictureBtn.setOnClickListener { openCamera() }

        setLocationBtn.setOnClickListener {
            val intent = Intent(requireContext(), EventsActivity::class.java)
            startActivity(intent)
        }

        seeLocationBtn.setOnClickListener {
            if(component != null && component!!.latitude != null && component!!.longitude != null){
                LocationHelper.setPinLocation(component?.latitude!!, component?.longitude!!)
                val intent = Intent(requireContext(), BasicMapActivity::class.java)
                startActivity(intent)
            }
        }
        componentPicture.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_reveal_left))
        takePictureBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_reveal_right))
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ComponentEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = component?._id
        if (id == null) {
            component = Component(
                "",
                "",
                0,
                "01-01-2020",
                false,
                AuthRepository.getUsername(),
                "",
                0,
                "",
                0f,
                0f
            );
        } else {
            viewModel.getComponentById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update items")
                if (it != null) {
                    component = it
                    name.setText(it.name)
                    quantity.setText(it.quantity.toString())

                    val df = SimpleDateFormat("dd.MM.yyyy")
                    val date: Date = df.parse(component!!.releaseDate)
                    val cal = Calendar.getInstance()
                    cal.time = date

                    datePicker.updateDate(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )
                    inStock.isChecked = it.inStock
                    component?.picturePath?.let { componentPicture.setImageURI(Uri.parse(component?.picturePath)) }
                    latitudeTxt.text = "${getString(R.string.longitude)} ${it.latitude}"
                    longitudeTxt.text = "${getString(R.string.longitude)} ${it.longitude}"
                }
            })
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    null
                }
                Log.d(TAG, "photofile $photoFile")
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.ficiapp.fileprovider",
                        it
                    )
                    Log.d(TAG, "photoURI: $photoURI");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var f = File.createTempFile("PHOTO_${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
        component?.picturePath = currentPhotoPath
        return f
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
                componentPicture.setImageURI(uri)
            }
        }
    }

    private fun initLocation() {
        val location = LocationHelper.getLocationAndClear()
        if(location.first != 0f || location.second != 0f) {
            latitudeTxt.text = "${getString(R.string.longitude)} ${location.first}"
            longitudeTxt.text = "${getString(R.string.longitude)} ${location.second}"
            component?.latitude = location.first
            component?.longitude = location.second
        }
    }
}