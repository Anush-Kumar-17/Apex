package Apex.app.service;

import Apex.app.exception.CsvProcessingException;
import Apex.app.exception.UserNotFoundException;
import Apex.app.model.UserCsvData;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Service
public class CsvReaderService {

    @Value("${app.csv.file.path:src/main/resources/user_data.csv}")
    private String csvFilePath;

    public UserCsvData findUserById(String userId) {
        try {
            List<UserCsvData> users = readCsvFile();

            Optional<UserCsvData> userOpt = users.stream()
                    .filter(user -> userId.equals(user.getUserId()))
                    .findFirst();

            if (userOpt.isEmpty()) {
                throw new UserNotFoundException("User ID '" + userId + "' not found in our records");
            }

            return userOpt.get();

        } catch (FileNotFoundException e) {
            throw new CsvProcessingException("User data file not found", e);
        } catch (IOException e) {
            throw new CsvProcessingException("Error reading user data file", e);
        } catch (Exception e) {
            if (e instanceof UserNotFoundException) {
                throw e;  // Re-throw user not found exception
            }
            throw new CsvProcessingException("Error processing user data: " + e.getMessage(), e);
        }
    }

    private List<UserCsvData> readCsvFile() throws IOException {
        try {
            return new CsvToBeanBuilder<UserCsvData>(new FileReader(csvFilePath))
                    .withType(UserCsvData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new IOException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }
}