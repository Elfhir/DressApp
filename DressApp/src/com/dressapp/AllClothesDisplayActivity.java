package com.dressapp;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Cette Activity g�re le display de tous les habits sous forme de liste.
 */
public class AllClothesDisplayActivity extends Activity {
	
	/**
	 * Liste stockant tous les habits � afficher.
	 */
	private ArrayList<Cloth> clothes = new ArrayList<Cloth>();
	
	/**
	 * Fichier json contenant les donn�es de tous les habits.
	 */
	private JSONObject json;
	
	/**
	 * Layout de la vue.
	 */
	private TableLayout tableContainer;
	
	/**
	 * T�che asynchrone qui permet la lecture du contenu d'une url.
	 * Le lien avec l'API web se fait ici.
	 */
	private class ReadURLTask extends AsyncTask<String, Void, String> {

		/**
		 * T�che asynchrone � ex�cuter en background.
		 * @return String Retourne le contenu de l'URL sous forme de cha�ne de caract�res.
		 *
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... url_str)
		{
			return APIRequestsManager.getURIContent(url_str[0]);
		}
		
		/**
		 * Le code s'ex�cutera une fois la t�che de lecture de l'URL accomplie.
		 * Le param�tre est la cha�ne-r�sultat obtenue suite � la lecture du contenu de l'URL.
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute (String result)
		{
			try
			{
				/*
				 * Le contenu de l'url est un fichier JSON.
				 * On cr�e donc un nouvel objet JSON avec la cha�ne obtenue.
				 */
				json = new JSONObject (result);
				
				/*
				 * Le parser JSON s'occupe de transformer les donn�es obtenues
				 * en tableau d'habits (Cloth).
				 */
				clothes = JSONParser.parseClothes(json);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// On affiche les habits sous forme de lignes d'un tableau.
			displayClothsTable ();
		}
	}

	/**
	 * Actions ex�cut�es lors de la cr�ation de l'activit�.
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// D�finition de la vue correspondante � l'activit�
        setContentView(R.layout.all_clothes_display);
        
        // Lecture de l'url contenant un json de donn�es de tous les habits de la BDD.
        //new ReadURLTask().execute("http://dressapp.alwaysdata.net/api/v1/clothes/");
	}
	
	/**
	 * Actions ex�cut�es lorsque l'activit� redevient l'activit� courante, apr�s avoir �t� paus�e
	 * (revient en haut de l'activity stack).
	 */
	@Override
	protected void onResume ()
	{
		super.onResume();
		
		// On r�cup�re le TableLayout.
    	tableContainer = (TableLayout) findViewById(R.id.tableContainer);
    	
    	// On supprime toutes les lignes du tableau qui y figuraient.
    	tableContainer.removeAllViews();
		
		/*
		 * R�cup�ration de la nouvelle liste d'habits : nouvelle requ�te vers l'API.
		 * Si on ne fait pas de nouvelle requ�te, on risque de se retrouver avec une liste
		 * d'habits obsol�te (exemple : l'utilisateur a �dit� le nom d'un habit puis revient en
		 * arri�re sur l'activit� "AllClothesDisplay". Le nom de l'habit n'y sera pas actualis�.
		 */
        new ReadURLTask().execute("http://dressapp.alwaysdata.net/api/v1/clothes/");
	}

	/**
	 * Affiche les habits sous forme de tableau.
	 */
	private void displayClothsTable ()
	{
		/*
		 * Si le tableau n'est pas vide, et donc
		 * si l'on a re�u des informations sur un habit ou plus,
		 */
        if (!clothes.isEmpty())
        {
        	// Compteur d'habits/de lignes
        	int i = 0;
        	
        	// On r�cup�re le TableLayout.
        	tableContainer = (TableLayout) findViewById(R.id.tableContainer);
        	
        	// Pour chaque habit dont on a les informations,
        	for (final Cloth currentCloth : clothes)
        	{
        		// On cr�e une nouvelle ligne dans le layout.
        		final TableRow row = new TableRow(getApplicationContext());
        		
        		/*
        		 * On cr�e �galement 3 nouveaux Views qui contiendront
        		 * les informations de l'habit : ils serviront � afficher
        		 * l'image, le nom et le type de l'habit sur une ligne.
        		 */
        		TextView labelImage = new TextView(getApplicationContext());
        		TextView labelName = new TextView(getApplicationContext());
        		TextView labelType = new TextView(getApplicationContext());
        		
        		// Les lignes impaires ont une couleur diff�rente.
        		if (i%2 != 0)
        		{
        			row.setBackgroundColor(Color.LTGRAY);
        		}
        		
        		// Les Views sont actualis�s avec des informations sur l'habit.
        		labelImage.setText(currentCloth.getColor1());
        		labelName.setText(currentCloth.getName());
        		labelType.setText(currentCloth.getCategory());
        		
        		// D�finition des paddings.
        		labelImage.setPadding(30, 5, 30, 5);
        		labelName.setPadding(20, 20, 20, 20);
        		labelType.setPadding(20, 20, 20, 20);
        		
        		// D�finition des couleurs de texte.
        		labelImage.setTextColor(Color.BLACK);
        		labelName.setTextColor(Color.BLACK);
        		labelType.setTextColor(Color.BLACK);
        		
        		// Ajout � la ligne du tableau des 3 Views.
        		row.addView(labelImage);
        		row.addView(labelName);
        		row.addView(labelType);
        		
        		/*
        		 * On ajoute � la ligne un �couteur sur le click.
        		 * Il permet de diriger l'utilisateur sur la page d'informations
        		 * de l'habit lorsque celui-ci clique sur la ligne correspondant �
        		 * cet habit.
        		 */
        		row.setOnClickListener(new View.OnClickListener() {
        			@Override
        			public void onClick(View v) {	        				
        				// On cr�e un nouvel intent qui m�nera � la page de l'habit.
        				Intent intent = new Intent (AllClothesDisplayActivity.this, ClothFormActivity.class);
        				
        				/**
        				 * On transmet � l'activit� suivante des donn�es via les Extras :
        				 * - le mode du formulaire. Ici il s'agit du mode VIEW car il
        				 * s'agit de la pr�visualisation d'un habit.
        				 * @see ClothFormActivity#e_Mode
        				 * - les donn�es de l'habit. Cela �vite d'avoir � refaire une requ�te
        				 * sur une autre URL. Les donn�es sont transmises sous forme de Bundle,
        				 * l'habit pourra ensuite �tre reconstitu� � partir de ce Bundle.
        				 * @see Cloth#toBundle()
        				 * @see Cloth#fromBundle()
        				 */
        				intent.putExtra("mode", ClothFormActivity.e_Mode.VIEW);
        				intent.putExtra("cloth", currentCloth.toBundle());
        				
        				// Une nouvelle activit� (pr�visualisation de l'habit) est lanc�e.
        				startActivity (intent);
        			}
        		});
        		
        		// On ajoute au layout la ligne pr�c�demment cr��e.
        		tableContainer.addView(row);
        		
        		// Incr�mentation du nombre d'habits.
        		++i;
        	}
        }
	}
	
}
