package com.example.languagetranslator;

import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    static final String TEXT_TO_SPEECH_API_KEY = "YOUR_API_KEY";
    static final String TEXT_TO_SPEECH_URL = "YOUR_URL";

    static final String SPEECH_TO_TEXT_API_KEY = "YOUR_API_KEY";
    static final String SPEECH_TO_TEXT_URL = "YOUR_URL";


    static final String LANGUAGE_TRANSLATOR_API_KEY = "YOUR_API_KEY";
    static final String LANGUAGE_TRANSLATOR_URL = "YOUR_URL";
    static final String LANGUAGE_TRANSLATOR_VERSION ="2018-05-01";

    public static final Map<String, String> TTS_VOICES = createTTSVoices();
    public static final Map<String, String> LANG_CODES = createLanguageCodes();
    public static final Map<String, String> STT_MODELS = createSTTModels();

    public static int[] FLAGS = {
            R.drawable.flag_us,
            R.drawable.flag_uk,
            R.drawable.flag_es,
            R.drawable.flag_fr,
            R.drawable.flag_de,
            R.drawable.flag_it,
            R.drawable.flag_ja,
            R.drawable.flag_ko,
            R.drawable.flag_pt,
            R.drawable.flag_nl
    };

    public static String[] LANGUAGES = {
            "American English",
            "British English",
            "Spanish",
            "French",
            "German",
            "Italian",
            "Japanese",
            "Korean",
            "Portuguese",
            "Dutch"
    };

    public static String[] LANG_SELECTION_ERROR_MESSAGES = {
            "The selected languages cannot be the same.",
            "The selected languages cannot be the same.",
            "Los idiomas seleccionados no pueden ser los mismos.",
            "Les langues sélectionnées ne peuvent pas être les mêmes.",
            "Die ausgewählten Sprachen können nicht identisch sein.",
            "Le lingue selezionate non possono essere le stesse.",
            "選択した言語を同じにすることはできません。",
            "선택한 언어는 동일 할 수 없습니다.",
            "Os idiomas selecionados não podem ser iguais.",
            "De geselecteerde talen kunnen niet hetzelfde zijn."
    };


    private static Map<String, String> createLanguageCodes(){
        Map<String, String> result = new HashMap<>();
        result.put("British English", "en");
        result.put("American English", "en");
        result.put("Spanish", "es");
        result.put("French", "fr");
        result.put("German", "de");
        result.put("Italian", "it");
        result.put("Japanese", "ja");
        result.put("Korean", "ko");
        result.put("Portuguese", "pt");
        result.put("Dutch", "nl");
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> createTTSVoices() {
        Map<String, String> result = new HashMap<>();
        result.put("BritishEnglish", SynthesizeOptions.Voice.EN_GB_KATEV3VOICE);
        result.put("AmericanEnglish", SynthesizeOptions.Voice.EN_US_OLIVIAV3VOICE);
        result.put("Spanish", SynthesizeOptions.Voice.ES_ES_LAURAV3VOICE);
        result.put("French", SynthesizeOptions.Voice.FR_FR_RENEEV3VOICE);
        result.put("German", SynthesizeOptions.Voice.DE_DE_BIRGITV3VOICE);
        result.put("Italian", SynthesizeOptions.Voice.IT_IT_FRANCESCAV3VOICE);
        result.put("Japanese", SynthesizeOptions.Voice.JA_JP_EMIV3VOICE);
        result.put("Korean", SynthesizeOptions.Voice.KO_KR_YOUNGMIVOICE);
        result.put("Portuguese", SynthesizeOptions.Voice.PT_BR_ISABELAV3VOICE);
        result.put("Dutch", SynthesizeOptions.Voice.NL_NL_EMMAVOICE);
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> createSTTModels(){
        Map<String, String> result = new HashMap<>();
        result.put("British English", "en-GB_BroadbandModel");
        result.put("American English", "en-US_BroadbandModel");
        result.put("Spanish", "es-CL_BroadbandModel");
        result.put("French", "fr-FR_BroadbandModel");
        result.put("German", "de-DE_BroadbandModel");
        result.put("Italian", "it-IT_BroadbandModel");
        result.put("Japanese", "ja-JP_BroadbandModel");
        result.put("Korean", "ko-KR_BroadbandModel");
        result.put("Portuguese", "pt-BR_BroadbandModel");
        result.put("Dutch", "nl-NL_BroadbandModel");
        return Collections.unmodifiableMap(result);
    }
}
