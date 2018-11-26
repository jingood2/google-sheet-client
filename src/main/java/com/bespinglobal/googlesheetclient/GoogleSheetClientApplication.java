package com.bespinglobal.googlesheetclient;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ArrayListMultimap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class GoogleSheetClientApplication {

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetClientApplication.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    }


    public static void main(String[] args) throws IOException, GeneralSecurityException {
        //SpringApplication.run(GoogleSheetClientApplication.class, args);
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "";
        //final String range = "Class Data!A2:E";
        final String range = "Dictionary!C:N";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .setMajorDimension("ROWS")
                .execute();
        List<List<Object>> values = response.getValues();
        values = response.getValues();

        Map<String, String> enMap = new HashMap<>();
        Map<String, String> koMap = new HashMap<>();
        Map<String, String> zhMap = new HashMap<>();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {

                /*
                System.out.printf("%s, %s\n", String.valueOf(row.get(0)),String.valueOf(row.get(7)));
                System.out.printf("%s, %s\n", String.valueOf(row.get(0)),String.valueOf(row.get(6)));
                System.out.printf("%s, %s\n", String.valueOf(row.get(0)),String.valueOf(row.get(5)));
                if("Y".equals(String.valueOf(row.get(7)))) {
                    //System.out.printf("%s, %s\n", String.valueOf(row.get(0)),row);
                    continue;
                }
                */
                // Print columns A and E, which correspond to indices 0 and 4.
                if(!"".equals(row.get(0))) {
                    //System.out.printf("%s, [%s,%s,%s]\n",row.get(0), row.get(3), row.get(4), row.get(5));
                    enMap.put(String.valueOf(row.get(0)), String.valueOf(row.get(3)));
                    koMap.put(String.valueOf(row.get(0)), String.valueOf(row.get(4)));
                    zhMap.put(String.valueOf(row.get(0)), String.valueOf(row.get(5)));
                }
            }
        }

        Map<String,Map<String,String>> mapByLang = new HashMap<>();

        mapByLang.put("en", enMap);
        mapByLang.put("ko", koMap);
        mapByLang.put("zh", zhMap);

        System.out.println(mapByLang.toString());
    }
}
