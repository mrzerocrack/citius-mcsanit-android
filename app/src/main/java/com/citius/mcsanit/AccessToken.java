package com.citius.mcsanit;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    public static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken(){
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"mcsanit\",\n" +
                    "  \"private_key_id\": \"9bdcef142860ce42c854317e5e4cf4a639232f88\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCqUSEc1q3MVZ6J\\nguHxLOIQjmDYG8zBYLOUuMFWHsYWDToTcMwqEPTtv7jkC9AcdK5qo2jvwAvy8Ubk\\nBPwP/qwxBGQcBv8cazcuWMpXAU/mffGdsvUhyyYAaLeohTqC9V3Yv6/E6A0X7B2c\\nlBgekICQkouO3hVrwzvmdyaKJOSfyVhj2yPz030WHbdGYj6sow/iXp0hAI2Udqi9\\noqKNStA92sXYgdEW/R9Mt+nRMd4r6QLD4UH2v3+J2fQ2GR/qVv6NJT6PDAHAYnfg\\nA2ZSmczHfDGuNVOwxUQfw0mkj2K+P62r9Efz1Ju5Wl9/cGlofwwjA8gcUYjV/e61\\nQiaiw/bNAgMBAAECggEAB8a3AJJTspvpJmWlq8WNF8zsYomYZm7kHCL4XtZ+XFTj\\nxS32AGiwSieiCTV53ca9DOzjlmi6EvBNuIz6tj8+POmyqW3vWWySwyi2KlVKwN8Y\\nuEbKtu2H0WZ2ljjpBEAwNfyAibi/mktDSdogJMsbt54dRZthtn8ePcZMaevHzXzI\\nnMKW6PW7M4WVOmWxDsP9lLotKZZ6FxHwNRmsLrDBRkP/mvaCCGI4wKA8D6ejf8qK\\nS9Xq60HjZBt+9DhXxTnWUdYzS0MOEf6s05qR/OvJuq8JngOj9xEiPeNp/l2gTS4H\\nrGG9Em/g2XXxpu3UOAvF7Thhce//LbJfgmIUulufgQKBgQDT7e708ELLpnUSkcu1\\nbHCqEgn5xvNGr0Zd2/dS6WYXIOUTMUmD+zYXnpJZyUZk49GV7h1cgM2wx/0meko5\\nvJYT131tPVZb3lvQC+7qSURQw0Rx2ykyCWZpwcq9VcSRNIU9lVZgq9bCM42/68jx\\ndA+EiyBzNVKoOsJsTgPXNFJGTQKBgQDNu/P0XLjeVJ7j45Xrcw4HYsyNTIW45HWA\\n7VuM3bZQF+j7kfirp8PwOuJ+TytkHOeSRpJcJwQKFu9BuhJcC5uyYOmImeiyDHuU\\ntF6cCNb6vwuvOPEkDY6F7ExxuB7s7pcCb8WgQY7RhAU1OOH0Jc0baRBBQwOokrcj\\noUNOQqGygQKBgQDCiqfQML0gNK1DXpKg9+p0NBr0Lq0jNcJiZomikp4knQ2rkJLr\\nlK8S/31Y7mqwMnEyMGLDh3lJzkuUBq4cUgFyNV3t6WBc9MFTOaEXcCpF+PnbilNG\\nm1pnun1hUFog8RVecrfYBeYeXzS/+LF89A1KnmwrVceKpmXTBcqAwobEAQKBgAeb\\nNlbHgtI9gK5eQLG2zEL7pSJoPN1sHOgKXoMjw1TMvYFnkTEPf9AG7U7MMIeABhPD\\nZi/gBWc2F9xvO7SJfVUn/hpdzlue2IbT8FeGgCQ/Ifh1e3zgv282IFFoBESwQYcG\\niOD264fVgLfKBZiYOTAVP1OaXa1C12CK2sku73mBAoGASiSspFmGhOdqOZQYkZVi\\n4i6xvNbcOf6iTDVRcKXhLSBne6hLd7ZL3zTa3zqTZjB3EsDYMWuRhbhkPbV0qyiH\\nuN9IPcf2OFj19ENbnwZxKGVGGfSuhrcEXZxDsLUnFdg4TNG3qUaZXPStgpW/EYVo\\nV0reotyfGGHWwwrUyqKSyGo=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-ifism@mcsanit.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"110330726350056757361\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ifism%40mcsanit.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        }catch (Exception e){
            Log.e("AccessTokenError", "GetAccessTOKEN: " + e.getLocalizedMessage());
            return null;
        }
    }
}
