package com.rigel.app.serviceimpl;

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
public class InvoiceGeneratorService {

	@Autowired
	private FySequenceRepository repository;

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
		int next = seq.getLastNumber() + 1;
		seq.setLastNumber(next);

		repository.save(seq);

		return seqCode + fyMonth + fyYear + userId + String.format("%08d", next);
	}
	
	@Transactional
	public String generateCustId(int userId, String seqCode, String seqName) {

		String fy = getFinancialYear(); // e.g. 04-2024
		String[] parts = fy.split("-");
		int fyMonth = Integer.parseInt(parts[0]);
		String fyYear = parts[1];

		// Try to fetch with lock
		FySequence seq = repository.findForUpdate(fyYear, fyMonth, userId, seqCode)
				.orElseGet(() -> createOrUpdateSequence(userId, seqCode, seqName, fyYear, fyMonth));

		// increment
		int next = seq.getLastNumber() + 1;
		seq.setLastNumber(next);

		repository.save(seq);

		return userId + String.format("%08d", next)+fyYear+fyMonth;
	}

	private FySequence createOrUpdateSequence(int userId, String seqCode, String seqName, String fyYear, int fyMonth) {

		Optional<FySequence> seqOrgOpt = repository.findSequence(userId, seqCode);

		if (seqOrgOpt.isEmpty()) {
        // New entry
			FySequence newSeq = FySequence.builder().fyYear(fyYear).fyMonth(fyMonth).lastNumber(0).userId(userId)
					.seqCode(seqCode).seqName(seqName).build();

			return repository.save(newSeq);
		}

		FySequence seq = seqOrgOpt.get();

       // ✅ ONLY reset if year changed
		if (!fyYear.equals(seq.getFyYear())) {
			seq.setFyYear(fyYear);
			seq.setFyMonth(fyMonth);
			seq.setLastNumber(0);
			return repository.save(seq);
		}

        // ✅ If same year → only update month (NO reset)
		if (seq.getFyMonth() != fyMonth) {
			seq.setFyMonth(fyMonth);
			return repository.save(seq);
		}

		return seq;
	}
}
