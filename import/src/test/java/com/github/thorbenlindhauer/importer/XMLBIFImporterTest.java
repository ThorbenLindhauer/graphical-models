package com.github.thorbenlindhauer.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.importer.xmlbif.XMLBIFImporter;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class XMLBIFImporterTest {

  protected XMLBIFImporter importer;
  protected InputStream inputStream;
  
  @Before
  public void setUp() {
    importer = new XMLBIFImporter();
  }
  
  @Test
  public void testDiagramImport() {
    inputStream = loadClasspathFile("simpleModel.bif.xml");
    
    List<GraphicalModel> importedModels = importer.importFromStream(inputStream);
    assertThat(importedModels).hasSize(1);
    
    GraphicalModel importedModel = importedModels.get(0);
    Scope scope = importedModel.getScope();
    
    assertThat(scope.getVariables()).hasSize(3);
    
    assertThat(scope.getVariables()).containsExactly(new DiscreteVariable("A", 2), 
        new DiscreteVariable("B", 2), new DiscreteVariable("C", 2));

    assertThat(importedModel.getFactors()).hasSize(3);
    
    for (DiscreteFactor factor : importedModel.getFactors()) {
      if (factor.getVariables().getVariables().size() == 1 && factor.getVariables().has("A")) {
        
        assertThat(factor.getValueForAssignment(new int[]{ 0 })).isEqualTo(0.3d);
        assertThat(factor.getValueForAssignment(new int[]{ 1 })).isEqualTo(0.7d);
        
      } else if (factor.getVariables().getVariables().size() == 1 && factor.getVariables().has("B")) {
        
        assertThat(factor.getValueForAssignment(new int[]{ 0 })).isEqualTo(0.9d);
        assertThat(factor.getValueForAssignment(new int[]{ 1 })).isEqualTo(0.1d);
        
      } else if (factor.getVariables().getVariables().size() == 3 && factor.getVariables().has("C")
          && factor.getVariables().has("A") && factor.getVariables().has("B")) {
        
        // order of assignments follows the import format here, ie values appear in the same order as in the document
        assertThat(factor.getValueForAssignment(new int[]{ 0, 0, 0 })).isEqualTo(0.6d);
        assertThat(factor.getValueForAssignment(new int[]{ 0, 0, 1 })).isEqualTo(0.4d);
        assertThat(factor.getValueForAssignment(new int[]{ 0, 1, 0 })).isEqualTo(0.7d);
        assertThat(factor.getValueForAssignment(new int[]{ 0, 1, 1 })).isEqualTo(0.3d);
        assertThat(factor.getValueForAssignment(new int[]{ 1, 0, 0 })).isEqualTo(0.8d);
        assertThat(factor.getValueForAssignment(new int[]{ 1, 0, 1 })).isEqualTo(0.2d);
        assertThat(factor.getValueForAssignment(new int[]{ 1, 1, 0 })).isEqualTo(0.9d);
        assertThat(factor.getValueForAssignment(new int[]{ 1, 1, 1 })).isEqualTo(0.1d);
        
      } else {
        fail("Unexpected factor scope: " + factor.getVariables().getVariableIds());
      }
    }
  }
  
  public void testStateLabelMatching() {
    inputStream = loadClasspathFile("simpleModel.bif.xml");
    
    // TODO: implement. Importer should be return a data structure that holds the model as well as encodes labels for variable outcomes
  }
  
  @After
  public void tearDown() throws IOException {
    inputStream.close();
  }
  
  protected InputStream loadClasspathFile(String path) {
    return this.getClass().getClassLoader().getResourceAsStream(path);
  }
}
