package implementations.FinalProjStocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;

public class PersonMain {

    static Person[] jsonToPersonArray;

    public static void main(String[] args) throws Exception {

        PersonMain personMain = new PersonMain();
        personMain.jsonPlay();

        System.currentTimeMillis();
    }


    public void jsonPlay() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        jsonToPersonArray = objectMapper.readValue(new File("src/main/java/FinalProjStocks/Person.json"), Person[].class);

        objectMapper.writeValue(new File("src/main/java/FinalProjStocks/PersonOut.json"), jsonToPersonArray);

    }

}
