/* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.thorbenlindhauer.importer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.github.thorbenlindhauer.cluster.generation.CliqueTreeGenerator;
import com.github.thorbenlindhauer.cluster.generation.ClusterGraphExportListener;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefUpdateContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductContextFactory;
import com.github.thorbenlindhauer.importer.xmlbif.XMLBIFImporter;
import com.github.thorbenlindhauer.inference.ExactInferencer;
import com.github.thorbenlindhauer.inference.GeneratedCliqueTreeInferencer;
import com.github.thorbenlindhauer.inference.NaiveInferencer;
import com.github.thorbenlindhauer.inference.VariableEliminationInferencer;
import com.github.thorbenlindhauer.inference.variableelimination.MinFillEliminationStrategy;
import com.github.thorbenlindhauer.inference.variableelimination.RandomEliminationStrategy;
import com.github.thorbenlindhauer.network.GraphicalModel;


public class WorkScenario {

  protected InputStream inputStream;
  protected GraphicalModel graphicalModel;

  protected Offset<Double> TOLERATED_OFFSET = Offset.offset(0.0001d);

  @Before
  public void setUp() {
    inputStream = loadClasspathFile("work.xmlbif");

    GraphicalModelImporter importer = new XMLBIFImporter();

    List<GraphicalModel> graphicalModels = importer.importFromStream(inputStream);
    graphicalModel = graphicalModels.get(0);
  }

  protected InputStream loadClasspathFile(String path) {
    return this.getClass().getClassLoader().getResourceAsStream(path);
  }

  @Test
  @Ignore
  public void testNaiveInferencerMarginal() {
    ExactInferencer inferencer = new NaiveInferencer(graphicalModel);

    // prob of being late at work
    double prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 0 });
    assertThat(prob).isEqualTo(0.457875d, TOLERATED_OFFSET);

    // prob of being at work on time
    prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 1 });
    assertThat(prob).isEqualTo(0.542125d, TOLERATED_OFFSET);
  }

  @Test
  public void testRandomVEInferencerMarginal() {
    ExactInferencer inferencer = new VariableEliminationInferencer(graphicalModel, new RandomEliminationStrategy());

    long startTime = System.currentTimeMillis();

    double prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 0 });
    assertThat(prob).isEqualTo(0.457875d, TOLERATED_OFFSET);

    prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 1 });
    assertThat(prob).isEqualTo(0.542125d, TOLERATED_OFFSET);

    // just some performance testing
    int repetitions = 1000;
    String[] variableIds = graphicalModel.getScope().getVariableIds();
    for (int i = 0; i < repetitions; i++) {
      String variableId = variableIds[repetitions % variableIds.length];

      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 0 });
      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 1 });
    }
    long endTime = System.currentTimeMillis();

    System.out.println("Test took " + (endTime - startTime) + " millis");
  }

  @Test
  public void testMinFillVEInferencerMarginal() {
    ExactInferencer inferencer = new VariableEliminationInferencer(graphicalModel, new MinFillEliminationStrategy());

    long startTime = System.currentTimeMillis();
    double prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 0 });
    assertThat(prob).isEqualTo(0.457875d, TOLERATED_OFFSET);

    prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 1 });
    assertThat(prob).isEqualTo(0.542125d, TOLERATED_OFFSET);

    // calculate each variable's marginal once
    int repetitions = 1000;
    String[] variableIds = graphicalModel.getScope().getVariableIds();
    for (int i = 0; i < repetitions; i++) {
      String variableId = variableIds[repetitions % variableIds.length];

      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 0 });
      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 1 });
    }
    long endTime = System.currentTimeMillis();

    System.out.println("Test took " + (endTime - startTime) + " millis");

  }

  @Test
  public void testGeneratedCliqueTreeSumProductMarginal() {
    CliqueTreeGenerator cliqueTreeGenerator = new CliqueTreeGenerator();
//    cliqueTreeGenerator.registerListener(CliqueTreeGenerator.CLUSTER_GRAPH_CREATED_EVENT,
//        new ClusterGraphExportListener("testGeneratedCliqueTreeSumProductMarginal.dot"));

    ExactInferencer inferencer = new GeneratedCliqueTreeInferencer(graphicalModel, cliqueTreeGenerator, new SumProductContextFactory());

    long startTime = System.currentTimeMillis();
    double prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 0 });
    assertThat(prob).isEqualTo(0.457875d, TOLERATED_OFFSET);

    prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 1 });
    assertThat(prob).isEqualTo(0.542125d, TOLERATED_OFFSET);

    // calculate each variable's marginal once
    int repetitions = 1000;
    String[] variableIds = graphicalModel.getScope().getVariableIds();
    for (int i = 0; i < repetitions; i++) {
      String variableId = variableIds[repetitions % variableIds.length];

      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 0 });
      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 1 });
    }
    long endTime = System.currentTimeMillis();

    System.out.println("Test took " + (endTime - startTime) + " millis");

  }

  @Test
  public void testGeneratedCliqueTreeBeliefPropagationMarginal() {
    CliqueTreeGenerator cliqueTreeGenerator = new CliqueTreeGenerator();
//    cliqueTreeGenerator.registerListener(CliqueTreeGenerator.CLUSTER_GRAPH_CREATED_EVENT,
//        new ClusterGraphExportListener("testGeneratedCliqueTreeBeliefPropagationMarginal.dot"));

    ExactInferencer inferencer = new GeneratedCliqueTreeInferencer(graphicalModel, cliqueTreeGenerator, new BeliefUpdateContextFactory());

    long startTime = System.currentTimeMillis();
    double prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 0 });
    assertThat(prob).isEqualTo(0.457875d, TOLERATED_OFFSET);

    prob = inferencer.jointProbability(graphicalModel.getScope().subScope("Lateatwork"), new int[] { 1 });
    assertThat(prob).isEqualTo(0.542125d, TOLERATED_OFFSET);

    // calculate each variable's marginal once
    int repetitions = 1000;
    String[] variableIds = graphicalModel.getScope().getVariableIds();
    for (int i = 0; i < repetitions; i++) {
      String variableId = variableIds[repetitions % variableIds.length];

      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 0 });
      prob = inferencer.jointProbability(graphicalModel.getScope().subScope(variableId), new int[] { 1 });
    }
    long endTime = System.currentTimeMillis();

    System.out.println("Test took " + (endTime - startTime) + " millis");

  }

  @After
  public void tearDown() throws IOException {
    inputStream.close();
  }
}
