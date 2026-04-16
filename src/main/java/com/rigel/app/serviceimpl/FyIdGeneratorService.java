package com.rigel.app.serviceimpl;

import java.time.LocalDate;

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

	@Transactional
	public String generateFyId(int userId, String formate) {
//		String formate = numberFormateName.split("-")[1];
		String fy = getFinancialYear(); // FY24
		int fyMonth = Integer.valueOf(fy.split("-")[0]);
		String fyYear = fy.split("-")[1];
		FySequence seq = repository.findForUpdate(fyYear, fyMonth, userId, formate).orElse(null);
		if (seq == null) {
			FySequence seqOrg = repository.findSequence(userId, formate).orElse(null);
			if (seqOrg == null) {
				seq = new FySequence(fyYear, fyMonth, 0, userId, formate);
			} else {

				FySequence seq1 = repository.findSequenceByMonth(fyMonth, userId, formate).orElse(null);
				if (seq1 == null) {
					seqOrg.setFyMonth(fyMonth);
					seq = repository.save(seqOrg);
				} else {
					FySequence seq2 = repository.findForUpdate(fyYear, fyMonth, userId, formate).orElse(null);
					if (seq2 == null) {
						seq1.setFyYear(fyYear);
						seq1.setLastNumber(0);
						seq = repository.save(seq1);
					}
				}
			}
		}

		int next = seq.getLastNumber() + 1;
		seq.setLastNumber(next);
		repository.save(seq);

		return formate+fyMonth + fyYear + userId + String.format("%05d", next);
	}

	private String getFinancialYear() {
		LocalDate today = LocalDate.now();
		int year = today.getYear() % 100;

		// India FY: April se start
		if (today.getMonthValue() < 4) {
			year--;
		}
		return today.getMonthValue() + "-" + String.format("%02d", year);
	}
}
