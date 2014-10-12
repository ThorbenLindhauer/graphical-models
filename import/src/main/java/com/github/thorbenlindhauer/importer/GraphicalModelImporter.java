package com.github.thorbenlindhauer.importer;

import java.io.InputStream;
import java.util.List;

import com.github.thorbenlindhauer.network.GraphicalModel;

/**
 * Subclasses implemnt importing of a certain serliazation structure of a graphicahl model.
 * 
 * @author Thorben
 */
public interface GraphicalModelImporter {

  List<GraphicalModel> importFromStream(InputStream inputStream);
}
