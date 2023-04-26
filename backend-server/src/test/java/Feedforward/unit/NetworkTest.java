package Feedforward.unit;

import io.github.cdimascio.dotenv.Dotenv;

import com.implementations.models.FeedForward.components.Connection;
import com.implementations.models.FeedForward.components.HiddenNode;
import com.implementations.models.FeedForward.components.InputNode;
import com.implementations.models.FeedForward.components.NNObj;
import com.implementations.models.FeedForward.components.Network;
import com.implementations.models.FeedForward.components.NetworkTrainer;
import com.implementations.models.FeedForward.components.OutputNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Test()
public class NetworkTest {
    @DataProvider
    public Object[][] data() {
        return new NNObj[][][]{
              new NNObj[][]{xorTrainingList(), xorTestingList()}
        };
    }

    @BeforeClass
    public void beforeClass(){
        Dotenv.configure().systemProperties().load();
    }

    @Test(dataProvider = "data")
    public void testNodeLayerSizes(NNObj[][] data) throws IOException {
        Network network = new Network(data[0], data[1]);
        Assert.assertEquals(network.getInputLayerNodes().length, 2);
        Assert.assertEquals(network.getHiddenLayerNodes(0).length, 2);
        Assert.assertEquals(network.getOutputLayerNodes().length, 1);
    }

    @Test(dataProvider = "data")
    public void testFeedForwardPassOutputs(NNObj[][] data) throws IOException {
        Network network = setupExampleNetwork(data);
        HiddenNode H1 = network.getHiddenLayerNodes(0)[0];
        HiddenNode H2 = network.getHiddenLayerNodes(0)[1];
        OutputNode O3 = network.getOutputLayerNodes()[0];

        NetworkTrainer networkTrainer = new NetworkTrainer(network);
        networkTrainer.trainNetwork(data[0], 1);

        Assert.assertEquals(round(H1.getOutputVal(), 2), 0.55);
        Assert.assertEquals(round(H2.getOutputVal(), 2), 0.27);
        Assert.assertEquals(round(O3.getOutputVal(), 2), 0.64);
    }

    @Test(dataProvider = "data")
    public void testErrorSignal(NNObj[][] data) throws IOException {
        Network network = setupExampleNetwork(data);
        OutputNode O3 = network.getOutputLayerNodes()[0];

        NetworkTrainer networkTrainer = new NetworkTrainer(network);
        networkTrainer.trainNetwork(data[0], 1);

        Assert.assertEquals(round(O3.calculateError(), 2), 0.06);
    }

    @Test(dataProvider = "data")
    public void testConnectionWeightChange(NNObj[][] data) throws IOException {
        Network network = setupExampleNetwork(data);
        HiddenNode H1 = network.getHiddenLayerNodes(0)[0];
        HiddenNode H2 = network.getHiddenLayerNodes(0)[1];
        OutputNode O3 = network.getOutputLayerNodes()[0];
        InputNode A = network.getInputLayerNodes()[0];
        InputNode B = network.getInputLayerNodes()[1];
        Connection O3_H1 = H1.outputConnectionsMap.get(O3.getId());
        Connection O3_H2 = H2.outputConnectionsMap.get(O3.getId());
        Connection H2_IN_A = H2.inputConnectionsMap.get(A.getId());
        Connection H1_IN_A = H1.inputConnectionsMap.get(A.getId());

        NetworkTrainer networkTrainer = new NetworkTrainer(network);
        networkTrainer.trainNetwork(data[0], 1);

        Assert.assertEquals(round(O3_H1.getWeight(), 4), 0.3168);
        Assert.assertEquals(round(O3_H2.getWeight(), 4), -0.3916);
        Assert.assertEquals(round(H2_IN_A.getWeight(), 4), 0.5998);
        Assert.assertEquals(round(H1_IN_A.getWeight(), 4), 0.7002);
    }

    // Takes about 7500 cycles to learn well
    public void testLearning() throws IOException {
        NNObj[][] data = new NNObj[][]{xorListFull(), xorListFull()};
        Network network = setupExampleNetwork(data);
        NetworkTrainer networkTrainer = new NetworkTrainer(network);
        networkTrainer.trainNetwork(data[0], 7500);
        for(NNObj iteration: data[1]){
            double[] result = network.processInput(iteration);
            Assert.assertEquals(round(result[0], 2), iteration.getOutputVals()[0]);
        }
    }

    NNObj[] xorTrainingList() {
        return new NNObj[]{
                new NNObj(new double[]{0.1, 0.9}, new double[]{0.9}, "1")
        };
    }

    NNObj[] xorTestingList() {
        return new NNObj[]{
                new NNObj(new double[]{0.9, 0.9}, new double[]{0.1}, "1_t")
        };
    }

    NNObj[] xorListFull() {
        return new NNObj[]{
                new NNObj(new double[]{0.1, 0.9}, new double[]{0.9}, "1_9"),
                new NNObj(new double[]{0.1, 0.1}, new double[]{0.1}, "1_1"),
                new NNObj(new double[]{0.9, 0.9}, new double[]{0.1}, "9_9"),
                new NNObj(new double[]{0.9, 0.1}, new double[]{0.9}, "9_1")
        };
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    Network setupExampleNetwork(NNObj[][] data) throws IOException {
        Network network = new Network(data[0], data[1]);

        HiddenNode H1 = network.getHiddenLayerNodes(0)[0];
        HiddenNode H2 = network.getHiddenLayerNodes(0)[1];
        OutputNode O3 = network.getOutputLayerNodes()[0];
        InputNode A = network.getInputLayerNodes()[0];
        InputNode B = network.getInputLayerNodes()[1];

        H1.setBiasWeight(0.4);
        H2.setBiasWeight(-0.5);
        O3.setBiasWeight(0.5);

        H1.inputConnectionsMap.get(A.getId()).setWeight(0.7);
        H1.inputConnectionsMap.get(B.getId()).setWeight(-0.3);
        H1.outputConnectionsMap.get(O3.getId()).setWeight(0.3);
        H2.inputConnectionsMap.get(A.getId()).setWeight(0.6);
        H2.inputConnectionsMap.get(B.getId()).setWeight(-0.6);
        H2.outputConnectionsMap.get(O3.getId()).setWeight(-0.4);

        return network;
    }
}