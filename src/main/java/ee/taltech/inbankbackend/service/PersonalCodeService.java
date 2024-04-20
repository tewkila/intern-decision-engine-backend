package ee.taltech.inbankbackend.service;

import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;

/**
 * Service for handling operations related to personal codes, including extracting
 * and calculating age from Estonian personal ID numbers.
 */
@Service
public class PersonalCodeService {

    /**
     * Extracts the age from a personal ID code by parsing the birth date embedded within the code.
     *
     * @param personalCode The personal ID code of an individual, which contains their birth date.
     * @return The age of the individual based on the birth date encoded in the personal ID.
     */
    public int extractAgeFromPersonalCode(String personalCode) {
        LocalDate birthDate = parseBirthDate(personalCode);
        return calculateAge(birthDate);
    }

    /**
     * Parses the birth date from the Estonian personal ID code.
     *
     * @param personalCode A string representing the Estonian personal ID.
     * @return A LocalDate object representing the person's birth date.
     */
    private LocalDate parseBirthDate(String personalCode) {
        int yearPrefix = getYearPrefix(personalCode.charAt(0));
        int year = yearPrefix + Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));
        return LocalDate.of(year, month, day);
    }

    /**
     * Calculates the age based on the birth date and the current date.
     *
     * @param birthDate The birth date to calculate the age from.
     * @return The calculated age in years.
     */
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }



    /**
     * Determines the century prefix for the birth year based on the first digit of the personal ID code.
     * This helps in reconstructing the full year from the last two digits provided in the ID.
     *
     * @param firstDigit The first character of the personal ID code, indicating the century and gender.
     * @return The century prefix (1800, 1900, or 2000) based on the provided digit.
     * @throws IllegalArgumentException if the first digit does not correspond to any known century prefix.
     */
    private int getYearPrefix(char firstDigit) {
        switch (firstDigit) {
            case '1': case '2': return 1800;
            case '3': case '4': return 1900;
            case '5': case '6': return 2000;
            default: throw new IllegalArgumentException("Invalid century digit in personal ID");
        }
    }
}
