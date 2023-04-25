package QAP.unit;

import io.github.cdimascio.dotenv.Dotenv;
import com.implementations.models.QuadraticAssignment.QAPSolver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

@Test()
public class QAPSolverTest {
    @DataProvider
    public Object[][] data() throws Exception {
        QAPSolver QAPSolver1 = new QAPSolver("src/test/java/QAP/resources/qap4.txt");
        QAPSolver1.runSolution();
        return new QAPSolver[][]{new QAPSolver[]{QAPSolver1}};
    }

    @BeforeClass
    public void beforeClass() {
        Dotenv.configure().systemProperties().load();
    }

    @Test(dataProvider = "data")
    public void testPermutationCount(QAPSolver QAPSolver) {
        Assert.assertEquals(QAPSolver.countResults(), 24);
    }

    @Test(dataProvider = "data")
    public void testBestPermutation(QAPSolver QAPSolver) {
        int[] bestPermutation = QAPSolver.getBestPermutation();
        int bestPermutationCost = QAPSolver.calculateCost(bestPermutation);
        Assert.assertEquals(bestPermutationCost, 35);
    }

    public void testPermutationCostCalculation() throws FileNotFoundException {
        QAPSolver qapSolver = new QAPSolver("src/test/java/QAP/resources/qap4_Realistic.txt");
        qapSolver.readInData();
        int[] testPermutation = new int[]{1,0,3,2};
        int cost = qapSolver.calculateCost(testPermutation);
        Assert.assertEquals(cost, 419);
    }
}