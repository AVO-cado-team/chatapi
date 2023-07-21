package sk.avo.chatapi.domain.service;

import sk.avo.chatapi.domain.model.filestorage.FileNotFoundException;

import java.io.File;
import java.util.UUID;

public interface FileStorageService {
  UUID storeFile(final File file);
  File getFile(final UUID fileId) throws FileNotFoundException;
}
