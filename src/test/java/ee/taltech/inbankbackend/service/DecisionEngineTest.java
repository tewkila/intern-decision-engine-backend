package ee.taltech.inbankbackend.service;

import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DecisionEngineTest {

    @InjectMocks
    private DecisionEngine decisionEngine;

    @Mock
    private PersonalCodeService personalCodeService;  // Mock the PersonalCodeService

    @BeforeEach
    void setUp() {
        // Setup common test data or configurations here if needed
    }

    @Test
    void testDebtorPersonalCode() throws NoValidLoanException {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);
        assertThrows(NoValidLoanException.class,
                () -> decisionEngine.calculateApprovedLoan("37605030299", 4000L, 12));
    }

    @Test
    void testSegment1PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);
        Decision decision = decisionEngine.calculateApprovedLoan("50307172740", 4000L, 12);
        assertEquals(2000, decision.getLoanAmount());
        assertEquals(20, decision.getLoanPeriod());
    }

    @Test
    void testSegment2PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);
        Decision decision = decisionEngine.calculateApprovedLoan("38411266610", 4000L, 12);
        assertEquals(3600, decision.getLoanAmount());
        assertEquals(12, decision.getLoanPeriod());
    }

    @Test
    void testSegment3PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);
        Decision decision = decisionEngine.calculateApprovedLoan("35006069515", 4000L, 12);
        assertEquals(10000, decision.getLoanAmount());
        assertEquals(12, decision.getLoanPeriod());
    }

    @Test
    void testInvalidPersonalCode() {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);
        assertThrows(InvalidPersonalCodeException.class,
                () -> decisionEngine.calculateApprovedLoan("12345678901", 4000L, 12));
    }

    @Test
    void whenApplicantIsUnderage_thenThrowNoValidLoanException() {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(17);  // Underage

        String personalCode = "underageExampleCode";
        assertThrows(NoValidLoanException.class,
                () -> decisionEngine.calculateApprovedLoan(personalCode, 5000L, 24),
                "Applicant is underage");
    }

    @Test
    void whenApplicantIsOfValidAge_thenApproveLoan() throws InvalidPersonalCodeException, NoValidLoanException, InvalidLoanAmountException, InvalidLoanPeriodException {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(30);  // Valid age

        String personalCode = "validAgeExampleCode";
        Decision decision = decisionEngine.calculateApprovedLoan(personalCode, 5000L, 24);
        assertNotNull(decision);
        assertEquals(5000L, decision.getLoanAmount().longValue());
        assertEquals(24, decision.getLoanPeriod().intValue());
    }

    @Test
    void whenApplicantIsOverage_thenThrowNoValidLoanException() {
        when(personalCodeService.extractAgeFromPersonalCode(anyString())).thenReturn(74);  // Overage

        String personalCode = "overageExampleCode";
        assertThrows(NoValidLoanException.class,
                () -> decisionEngine.calculateApprovedLoan(personalCode, 5000L, 24),
                "Applicant is over the age limit");
    }
}

