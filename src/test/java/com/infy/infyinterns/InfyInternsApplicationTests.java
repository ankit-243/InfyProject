package com.infy.infyinterns;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.service.ProjectAllocationService;
import com.infy.infyinterns.service.ProjectAllocationServiceImpl;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Any;
//this is just for test, new project 
//testing again for collabrative
@SpringBootTest
public class InfyInternsApplicationTests {

	@Mock
	private MentorRepository mentorRepository;
	@InjectMocks
	private ProjectAllocationService projectAllocationService = new ProjectAllocationServiceImpl();

	@Test
	public void allocateProjectCannotAllocateTest() throws Exception {
		Mentor mentor = new Mentor();
		mentor.setMentorId(123);
		mentor.setMentorName("maniteja");
		mentor.setNumberOfProjectsMentored(4);
		Optional<Mentor> op = Optional.of(mentor);
		Mockito.when(mentorRepository.findById(123)).thenReturn(op);
		ProjectDTO pro = new ProjectDTO();
		pro.setIdeaOwner(1);
		MentorDTO dto = new MentorDTO();
		dto.setMentorId(123);
		pro.setMentorDTO(dto );
		pro.setProjectName("hello");
		pro.setReleaseDate(LocalDate.now());
		InfyInternException ex = Assertions.assertThrows(InfyInternException.class, () -> projectAllocationService.allocateProject(pro));
		Assertions.assertEquals("Service.CANNOT_ALLOCATE_PROJECT", ex.getMessage());
	}

	@Test
	public void allocateProjectMentorNotFoundTest() throws Exception {
		Mockito.when(mentorRepository.findById(123)).thenReturn(Optional.empty());
		ProjectDTO pro = new ProjectDTO();
		pro.setIdeaOwner(1);
		MentorDTO dto = new MentorDTO();
		dto.setMentorId(123);
		pro.setMentorDTO(dto );
		pro.setProjectName("hello");
		pro.setReleaseDate(LocalDate.now());
		InfyInternException ex = Assertions.assertThrows(InfyInternException.class, () -> projectAllocationService.allocateProject(pro));
		Assertions.assertEquals("Service.MENTOR_NOT_FOUND", ex.getMessage());
	}
}
