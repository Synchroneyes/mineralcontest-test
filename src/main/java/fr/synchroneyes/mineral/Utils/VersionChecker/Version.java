package fr.synchroneyes.mineral.Utils.VersionChecker;

import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.UrlFetcher.Urls;
import fr.synchroneyes.mineral.mineralcontest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Version {

    public static boolean isUpdating = false;
    public static boolean hasUpdated = false;
    public static boolean isCheckingStarted = false;

    /**
     * Récupère tous les messages à partir du site web pour cette version du plugin
     *
     * @paramNonUsed threadedFetch - Utiliser un thread ou non pour l'utilisation de cette fonction
     * @param listToFill    - Une liste à remplir avec les messages du site
     */
    public static void fetchAllMessages(List<String> listToFill) {
        listToFill.clear();

        // On récupère la verison du plugin
        String currentVersion = mineralcontest.plugin.getDescription().getVersion();

        // On crée la nouvelle requete
        HttpPost request = new HttpPost(Urls.API_URL_GET_CURRENT_VERSION_MESSAGES);
        try {
            // On ajoute un paramètre à la requete
            List<NameValuePair> parametres = new ArrayList<>();
            parametres.add(new BasicNameValuePair("version", currentVersion));
            request.setEntity(new UrlEncodedFormEntity(parametres));
            HttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpResponse response = null;



            // On execute la requete
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            // On récupère le contenu de la requete
            String entityContents = EntityUtils.toString(entity);

            // On sait que le résultat est un tableau au format JSON, on le traite donc comme un tableau
            JSONArray reponse = new JSONArray(entityContents);
            // Si on reçois aucun message, on arrête
            if (reponse.isEmpty()) return;

            // Sinon, on rempli la liste
            for (int indexMessage = 0; indexMessage < reponse.length(); ++indexMessage)
                listToFill.add(Lang.translate(reponse.get(indexMessage).toString()));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void doCheck() {
        String currentVersion = mineralcontest.plugin.getDescription().getVersion();
        HttpPost request = new HttpPost(Urls.API_URL_LAST_VERSION_CHECK);
        try {
            List<NameValuePair> parametres = new ArrayList<>();
            parametres.add(new BasicNameValuePair("version", currentVersion));

            request.setEntity(new UrlEncodedFormEntity(parametres));

            HttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpResponse response = null;
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String entityContents = EntityUtils.toString(entity);


            // On récupère la réponse, plusieurs cas
            JSONObject reponse = new JSONObject(entityContents);
            if (reponse.getString("status").equals("update")) {
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.RED + " A new update is available, plugin will now auto-update to version " + reponse.getString("message"));
                isUpdating = true;
                DownloadNewVersion(reponse.getString("url"), reponse.getString("file_name"), reponse.get("file_size").toString(), reponse.get("message").toString());
            } else {
                isCheckingStarted = false;
            }

            if (reponse.getString("status").equals("same")) {
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Plugin is up-to-date!");
            }
            // Réponse du site web
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Check(boolean theadedCheck) {
        if (MapBuilder.getInstance().isBuilderModeEnabled) return;
        if (!isCheckingStarted) isCheckingStarted = true;

        if (theadedCheck) {
            Thread thread = new Thread(Version::doCheck);
            thread.start();
        } else {
            doCheck();
        }


    }


    /**
     * Converti une version (ex: 1.0.2) en "nombre" (ex: 102). En gros retire les "." ou les ","
     *
     * @param chaine
     * @return
     */
    private static String toVersion(String chaine) {
        chaine = chaine.replace(",", "");
        return chaine.replace(".", "");

    }

    private static void DownloadNewVersion(String url, String fileName, String fileSize, String version) throws InterruptedException {
        Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + "" + ChatColor.GOLD + " Downloading version " + version);
        Bukkit.broadcastMessage(mineralcontest.prefix + ChatColor.GOLD + " Downloading a new version of the plugin ...");

        try {

            File dossierTelechargement = new File("plugins");
            if (!dossierTelechargement.exists()) dossierTelechargement.mkdir();

            File fichierTelecharge = new File(dossierTelechargement, fileName);


            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            FileOutputStream fos = new FileOutputStream(fichierTelecharge);
            int inByte;

            // Taille d'un MO en byte
            int taille_mo = 1048576;
            int taille_fichier = Integer.parseInt(fileSize);
            double downloaded = 0;

            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
                downloaded++;

                if (downloaded % (taille_mo / 10) == 0)
                    Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download progress: " + ((downloaded / taille_fichier) * 100) + "%");
            }

            is.close();
            fos.close();

            client.close();

            Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download complete! Now reloading ...");
            isUpdating = false;
            hasUpdated = true;


        } catch (FileNotFoundException fno) {
            fno.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * Mettre 2 chaines de caractère à la même "longueur" en ajoutant des "0" à la fin.
     * Ex: s1 = "10" et s2 = "111", s1 va devenir "100" et s2 restera pareil
     *
     * @param s1
     * @param s2
     * @return
     */
    private String[] setStringToSameSize(String s1, String s2) {
        StringBuilder s1Builder = new StringBuilder(s1);
        StringBuilder s2Builder = new StringBuilder(s2);

        while (s1Builder.length() > s2.length())
            s1Builder.append("0");
        s1 = s1Builder.toString();

        while (s2Builder.length() > s1.length())
            s2Builder.append("0");
        s2 = s2Builder.toString();

        return new String[]{s1, s2};
    }
}
