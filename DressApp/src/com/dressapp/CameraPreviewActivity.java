package com.dressapp;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author Tha�s
 *
 */
public class CameraPreviewActivity extends Activity implements SurfaceHolder.Callback {
	
	private Camera camera;
	private SurfaceView surfaceCamera;
	private Button buttonTakePicture;
	private Boolean isPreview;
	private View.OnClickListener takePictureListener = new View.OnClickListener ()
    {

    	// Callback pour prise de photo.
	    Camera.PictureCallback pictureCallback = new Camera.PictureCallback ()
	    {

	    	public void onPictureTaken(byte[] data, Camera camera)
	    	{
				if (data != null)
				{					
				    // On r�cup�re les donn�es.			        
			        Intent intent = new Intent (CameraPreviewActivity.this, ClothFormActivity.class);
			        intent.putExtra("bitmapPicture", data);
					startActivity (intent);
				}
	    	}
	    	
	    };
	    
        public void onClick(View v)
        {
            // Prendre une photo
            if (camera != null)
            {
            	// D�s que l'image est pr�te, on appelle le callback.
            	camera.takePicture (null, null, null, pictureCallback);
            }
        }
    };
	
	/**
	 * Cr�ation de l'activit�
	 * 
	 * @since v1.0 2014-02-25
	 */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
	    super.onCreate (savedInstanceState);

	    getWindow().setFormat (PixelFormat.TRANSLUCENT);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    isPreview = false;
	    
	    setContentView (R.layout.camera_display);

	    // R�cup�ration de la surface.
	    surfaceCamera = (SurfaceView) findViewById (R.id.surfaceViewCamera);
	    
	    // R�cup�ration du bouton "take a picture"
	    buttonTakePicture = (Button) findViewById (R.id.buttonTakeAPicture);
	    
	    // Initialisation de la camera.
	    surfaceCamera.getHolder().addCallback (this);
        
	    // Ajout du listener sur la surface et le bouton
        surfaceCamera.setOnClickListener(takePictureListener);
        buttonTakePicture.setOnClickListener(takePictureListener);
        
	}

	@Override
	public void onResume ()
	{
	    super.onResume();
	    camera = Camera.open();
	}
	
	@Override
	public void onPause ()
	{
	    super.onPause();

	    if (camera != null)
	    {
	        camera.release();
	        camera = null;
	    }
	}
	
	/**
	 * Indique ce qu'il se passe lorsque la surface est modifi�e.
	 * 
	 * @since v1.0 2014-02-25
	 */
	@Override
	public void surfaceChanged (SurfaceHolder holder, int format, int width, int height)
	{
		if (camera == null)
		{
			return;
		}
		
		// On arr�te la pr�visualisation si elle �tait en cours.
	    if (isPreview)
	    {
	        camera.stopPreview ();
	        
	        // L'�tat de pr�visualisation est enlev�.
	        isPreview = false;
	    }
	    
	    // R�cup�ration des param�tres actuels
	    Camera.Parameters parameters = camera.getParameters ();
	    
	    if (parameters != null)
	    {
	    	// Mise � jour de la taille
		    parameters.setPreviewSize (width, height);

		    // Mise � jour des param�tres
		    camera.setParameters (parameters);
	    }

	    try
	    {
	        // La pr�visualisation est remise en place ;
	    	// On la rattache � la surface.
	        camera.setPreviewDisplay (surfaceCamera.getHolder());
	    } catch (IOException e)
	    {
	    }

	    // La pr�visualisation est relanc�e.
	    camera.startPreview ();

	    // L'�tat de pr�visualisation est remis � true.
	    isPreview = true;
	}

	/**
	 * Indique ce qu'il se passe lors de la cr�ation de la surface.
	 * 
	 * @since v1.0 2014-02-25
	 */
	@Override
	public void surfaceCreated (SurfaceHolder holder)
	{
		// Mise en marche du p�riph�rique
		if (camera == null)
		{
	        camera = Camera.open();
		}
	}

	/**
	 * Indique ce qu'il se passe lorsque la surface est supprim�e.
	 * 
	 * @since v1.0 2014-02-25
	 */
	@Override
	public void surfaceDestroyed (SurfaceHolder holder)
	{
		// Si la cam�ra existe,
		if (camera != null)
		{
			// Arr�t de la previsualisation
	        camera.stopPreview();
	        
	        // On signale qu'on n'est plus "en pr�visualisation"
	        isPreview = false;
	        
	        // Arr�t du p�riph�rique (cam�ra)
	        camera.release();
	    }
	}

}
