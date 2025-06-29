package com.gr.geias.service;

import java.io.IOException;

public interface JobImportService {

    void importFromJson(String classpathLocation) throws IOException;
}
