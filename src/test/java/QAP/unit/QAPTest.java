package QAP.unit;

import io.github.cdimascio.dotenv.Dotenv;
import models.GeneticAlgorithm.company.MainQAPSolution;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Test()
public class QAPTest {
    @DataProvider
    public Object[] data() throws IOException {
        MainQAPSolution mainQAPSolution1 = new MainQAPSolution();
        mainQAPSolution1.setFile("had122.txt");
        mainQAPSolution1.main();
        return new MainQAPSolution[]{mainQAPSolution1};
    }

    @BeforeClass
    public void beforeClass() {
        Dotenv.configure().systemProperties().load();
    }

    @Test(dataProvider = "data")
    public void testPermutationCount(MainQAPSolution mainQAPSolution) {
        Assert.assertEquals(mainQAPSolution.countResults(), 24);
    }

    @Test(dataProvider = "data")
    public void testBestPermutation(MainQAPSolution mainQAPSolution) {
        int[] bestPermutation = mainQAPSolution.getBestPermutation();
        int bestPermutationCost = mainQAPSolution.calculateCost(bestPermutation);
        Assert.assertEquals(bestPermutationCost, 70);
    }
}