package com.gr.geias.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JobImportService {

    void importFromJson(String classpathLocation) throws IOException;

    List<Map<String, Object>> getTopPreferredMajors();
}
