package com.rigel.app.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.model.FySequence;
import com.rigel.app.repo.FySequenceRepository;

import jakarta.transaction.Transactional;

@Lazy
@Service
public class FyIdGeneratorService {

	@Autowired
	private FySequenceRepository repository;

//	@Transactional
//	public String generateFyId(int userId, String seqCode,String seqName) {
////		String seqCode = seqCode.split("-")[1];
//		String fy = getFinancialYear(); // FY24
//		int fyMonth = Integer.valueOf(fy.split("-")[0]);
//		String fyYear = fy.split("-")[1];
//		FySequence seq = repository.findForUpdate(fyYear, fyMonth, userId, seqCode).orElse(null);
//		if (seq == null) {
//			System.out.println("sec-------"+seq+",fyYear"+fyYear+"--fyMonth-"+fyMonth+",userId-"+userId+",f--"+seqCode);	
//			
//			FySequence seqOrg = repository.findSequence(userId, seqCode).orElse(null);
//			if (seqOrg == null) {
//				FySequence newSeq = FySequence.builder()
//				        .fyYear(fyYear)
//				        .fyMonth(fyMonth)
//				        .lastNumber(0)
//				        .userId(userId)
//				        .seqCode(seqCode)
//				        .seqName(seqName)
//				        .build();
//				seq = newSeq;
//				seq = repository.save(newSeq);
//			} else {
//				FySequence seq1 = repository.findSequenceByMonth(fyMonth, userId, seqCode).orElse(null);
//				if (seq1 == null) {
//					seq=seqOrg;
//					seqOrg.setFyMonth(fyMonth);
//					seq = repository.save(seqOrg);
//				} else {
//					FySequence seq2 = repository.findForUpdate(fyYear, fyMonth, userId, seqCode).orElse(null);
//					if (seq2 == null) {
//						seq=seq1;
//						seq1.setFyYear(fyYear);
//						seq1.setFyMonth(fyMonth);
//						seq1.setLastNumber(0);
//						seq = repository.save(seq1);
//					}
//				}
//			}
//		}
//
//		int next = seq.getLastNumber() + 1;
//		seq.setLastNumber(next);
//		repository.save(seq);
//
//		return seqCode+fyMonth + fyYear + userId + String.format("%05d", next);
//	}

	private String getFinancialYear() {
		LocalDate today = LocalDate.now();
		int year = today.getYear() % 100;

		// India FY: April se start
		if (today.getMonthValue() < 4) {
			year--;
		}
		return today.getMonthValue() + "-" + String.format("%02d", year);
	}

	@Transactional
	public String generateFyId(int userId, String seqCode, String seqName) {

		String fy = getFinancialYear(); // e.g. 04-2024
		String[] parts = fy.split("-");
		int fyMonth = Integer.parseInt(parts[0]);
		String fyYear = parts[1];

		// Try to fetch with lock
		FySequence seq = repository.findForUpdate(fyYear, fyMonth, userId, seqCode)
				.orElseGet(() -> createOrUpdateSequence(userId, seqCode, seqName, fyYear, fyMonth));

		// increment
		int next = Integer.valueOf(seq.getLastNumber()) + 1;
		seq.setLastNumber(String.valueOf(next));

		repository.save(seq);

		return seqCode + fyMonth + fyYear + userId + String.format("%05d", next);
	}

	private FySequence createOrUpdateSequence(int userId, String seqCode, String seqName, String fyYear, int fyMonth) {

		// Try existing sequence
		Optional<FySequence> seqOrgOpt = repository.findSequence(userId, seqCode);

		if (seqOrgOpt.isEmpty()) {
			// Fresh entry
			FySequence newSeq = FySequence.builder().fyYear(fyYear).fyMonth(fyMonth).lastNumber(String.valueOf(0))
					.userId(userId).seqCode(seqCode).seqName(seqName).build();

			return repository.save(newSeq);
		}

		FySequence seqOrg = seqOrgOpt.get();

		// Check same month
		Optional<FySequence> seqMonthOpt = repository.findSequenceByMonth(fyMonth, userId, seqCode);
		if (seqMonthOpt.isEmpty()) {
			seqOrg.setFyMonth(fyMonth);
			return repository.save(seqOrg);
		}

		FySequence seqMonth = seqMonthOpt.get();

		// Reset for new FY
		seqMonth.setFyYear(fyYear);
		seqMonth.setFyMonth(fyMonth);
		seqMonth.setLastNumber(String.valueOf(0));

		return repository.save(seqMonth);
	}

	@Transactional
	public String generateItemCode(int userId, String seqCode) {
	    String fy = getFinancialYear(); // e.g. 04-2024
	    String[] parts = fy.split("-");
	    if (parts.length < 2) {
	        throw new IllegalArgumentException("Invalid financial year format: " + fy);
	    }
	    int fyMonth = Integer.parseInt(parts[0]);
	    String fyYear = parts[1];

	    // Fetch existing sequence
	    FySequence seq = repository.findSequence(userId, seqCode)
	        .orElseGet(() -> {
	            // First time entry → insert new record
	            FySequence newSeq = FySequence.builder()
	                    .fyYear(fyYear)
	                    .fyMonth(fyMonth)
	                    .lastNumber("0")
	                    .userId(userId)
	                    .seqCode(seqCode)
	                    .seqName("")
	                    .build();
	            return repository.save(newSeq);
	        });

	    // Null-safe lastNumber
	    String lastNumber = (seq.getLastNumber() == null || seq.getLastNumber().isBlank())
	            ? "0"
	            : seq.getLastNumber();

	    BigDecimal finalNumber;
	    try {
	        finalNumber = new BigDecimal(lastNumber).add(BigDecimal.ONE);
	    } catch (NumberFormatException e) {
	        finalNumber = BigDecimal.ONE; // fallback
	    }

	    // Update DB with new lastNumber
	    seq.setLastNumber(finalNumber.toPlainString());
	    repository.save(seq);

	    // Format code (e.g. padded with 3 digits)
	    String formattedNumber = String.format("%06d", finalNumber.intValue());

	    return seqCode + formattedNumber;
	}

}
