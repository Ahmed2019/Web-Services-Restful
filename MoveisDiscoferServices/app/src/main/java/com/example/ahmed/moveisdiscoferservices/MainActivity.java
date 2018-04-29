package com.example.ahmed.moveisdiscoferservices;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

public class MainActivity extends AppCompatActivity {

    private TextView tvData;

    String c="" ;
   // String url1="https://api.themoviedb.org/3/search/movie?api_key=e11c8d01cca6715f3a03d139c2d9aca9";
    String url2="https://api.themoviedb.org/3/discover/movie?api_key=e11c8d01cca6715f3a03d139c2d9aca9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Button search = (Button) findViewById(R.id.searchbutton);
        tvData = (TextView) findViewById(R.id.tvJsonItem);

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String Url="";
                new JSONTask().execute(url2);
            }

        });
    }


    public class JSONTask extends AsyncTask<String,String,String> {


        public boolean isInteger( String input ) {
            try {
                Integer.parseInt( input );
                return true;
            }
            catch( Exception e ) {
                return false;
            }
        }

        String data="";
        String In="";

        EditText UserInput = (EditText) findViewById(R.id.UserInput);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            In+=UserInput.getText();

            if (In.equals( "popular" )) {
                     /* popularity */
                data += "&sort_by=popularity.desc";
            } else if (In.equals( "Highst vote" )) {
                    /* Highest vote */
                data += "&certification_country=US&certification=R&sort_by=vote_average.desc";
            } else if (In.equals( "Best Drama" )) {
                     /* Best  Drama */
                data += "&with_genres=18&sort_by=vote_average.desc&vote_count.gte=10";
            } else if (isInteger( In )) {
                    /* Best movies in  Year */
                data += "&primary_release_year=" + UserInput.getText() + "&sort_by=vote_average.desc";
            }
            else
            {
                    /*Search  */     data+="&query="+UserInput.getText();
            }


        }

        @Override
        protected String doInBackground(String ... urls) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {

                URL url = new URL(urls[0] );
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput( true );


                OutputStreamWriter outputStreamWriter = new OutputStreamWriter( connection.getOutputStream() );
                outputStreamWriter.write( data );
                outputStreamWriter.flush();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finaljeson = buffer.toString();


                String original_title,original_language,release_date,overview;
                Integer vote_count,id;Double vote_average,popularity;


                JSONObject parentObject = new JSONObject( finaljeson );
                JSONArray ParentArray = parentObject.getJSONArray( "results" );

                StringBuffer stringBuffer =new StringBuffer();

                for(int i=0;i<ParentArray.length();++i) {
                    JSONObject finalObject = ParentArray.getJSONObject( i );

                    original_title = finalObject.getString( "original_title" );
                    original_language = finalObject.getString( "original_language" );
                    release_date =finalObject.getString( "release_date" );
                    vote_count = finalObject.getInt( "vote_count" );
                    popularity = finalObject.getDouble( "popularity" );
                    vote_average = finalObject.getDouble( "vote_average" );
                    id = finalObject.getInt( "id" );
                    overview =finalObject.getString( "overview" );

                    stringBuffer.append( "vote_count : " +vote_count+" \n "+"id : "+id+" \n "+"vote_average : "
                            +vote_average+" \n "+"popularity : "+popularity+" \n "
                            +"original_language : "+original_language+" \n "+"original_title : "+original_title+"\n"
                            +"release_date : "+release_date+"\nStory :"+overview+"\n******************************** \n");


                }

                return stringBuffer.toString();


            } catch (MalformedInputException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute( result );
            tvData.setText(result);
        }
    }
}
