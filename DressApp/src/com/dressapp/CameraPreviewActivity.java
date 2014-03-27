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
 * Cette activit� permet l'affichage de la cam�ra et la prise de photo.
 */
public class CameraPreviewActivity extends Activity implements SurfaceHolder.Callback {
	
	/**
	 * D�finit si l'on est en mode pr�visualisation ou non.
	 */
	private Boolean isPreview;
	
	/**
	 * Bouton clickable pour prendre une photo.
	 */
	private Button buttonTakePicture;
	
	/**
	 * Cam�ra utilis�e pour prendre la photo.
	 */
	private Camera camera;
	
	/**
	 * Surface sur laquelle est affich�e la pr�visualisation de la cam�ra.
	 */
	private SurfaceView surfaceCamera;
	
	/**
	 * Ecouteur de click : prend une photo lorsque l'on clique sur l'�l�ment.
	 */
	private View.OnClickListener takePictureListener = new View.OnClickListener ()
    {
    	/**
    	 * Callback pour prise de photo.
    	 */
	    Camera.PictureCallback pictureCallback = new Camera.PictureCallback ()
	    {
	    	/**
	    	 * Actions � ex�cuter d�s que la photo a �t� prise.
	    	 * @param byte[] Donn�es de la photo prise (donn�es d'image).
	    	 */
	    	public void onPictureTaken (byte[] data, Camera camera)
	    	{
				if (data != null)
				{					
				    /*
				     * On initialise un nouvel intent : une fois la photo prise,
				     * on est dirig� vers le formulaire de sauvegarde d'habit.	        
				     */
			        Intent intent = new Intent (CameraPreviewActivity.this, ClothFormActivity.class);
			        
			        /**
			         * On passe des donn�es � la prochaine activit� :
			         * - Le mode : en mode SAVE car le formulaire de sauvegarde d'habit
			         * doit s'afficher.
			         * @see {@link ClothForm#e_Mode}
			         * - Les donn�es de l'image.
			         */
			        intent.putExtra("mode", ClothFormActivity.e_Mode.SAVE);
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
     * Actions � ex�cuter d�s la cr�ation de l'activit�.
     */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
	    super.onCreate (savedInstanceState);
	    
	    if (MainActivity.user == null || !MainActivity.user.isConnected())
        {
        	// User d�connect� : on le renvoie au formulaire de connexion.
        	Intent intent = new Intent (CameraPreviewActivity.this, LoginFormActivity.class);
			startActivity (intent);
        }

	    // D�finition des param�tres de la fen�tre.
	    getWindow().setFormat (PixelFormat.TRANSLUCENT);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    // On n'est pas encore en mode pr�visualisation.
	    isPreview = false;
	    
	    // On associe la vue correspondante � l'activit�.
	    setContentView (R.layout.camera_display);

	    // R�cup�ration de la surface qui affichera la cam�ra.
	    surfaceCamera = (SurfaceView) findViewById (R.id.surfaceViewCamera);
	    
	    // R�cup�ration du bouton "take a picture"
	    buttonTakePicture = (Button) findViewById (R.id.buttonTakeAPicture);
	    
	    // Initialisation de la cam�ra.
	    surfaceCamera.getHolder().addCallback (this);
        
	    /*
	     * Ajout du listener sur la surface et sur le bouton :
	     * on pourra prendre des photos en cliquant n'importe o� sur l'�cran.
	     */
        surfaceCamera.setOnClickListener(takePictureListener);
        buttonTakePicture.setOnClickListener(takePictureListener);
        
	}

	/**
	 * D�finit les actions lorsque l'utilisateur reprend l'activit� depuis un �tat "en pause".
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume ()
	{
	    super.onResume();

	    if (MainActivity.user == null || !MainActivity.user.isConnected())
        {
        	// User d�connect� : on le renvoie au formulaire de connexion.
        	Intent intent = new Intent (CameraPreviewActivity.this, LoginFormActivity.class);
			startActivity (intent);
        }
	    
	    // On ouvre � nouveau la cam�ra.
	    camera = Camera.open();
	}
	
	/**
	 * D�finit les actions � ex�cuter lorsque l'application est mise en pause.
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause ()
	{
	    super.onPause();

	    // Si la cam�ra existe,
	    if (camera != null)
	    {
	    	// On stoppe la cam�ra.
	        camera.release();
	        camera = null;
	    }
	}
	
	/**
	 * Indique ce qui se passe lorsque la surface d'affichage est modifi�e.
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
