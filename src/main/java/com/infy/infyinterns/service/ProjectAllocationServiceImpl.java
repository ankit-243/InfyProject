package com.infy.infyinterns.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;
import com.infy.infyinterns.entity.*;

@Service("projectService")
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

	@Autowired
	MentorRepository mentorRepository;
	@Autowired
	ProjectRepository projectRepository;
	
	@Override
	public Integer allocateProject(ProjectDTO project) throws InfyInternException {
		Mentor mentor = mentorRepository.findById(project.getMentorDTO().getMentorId())
				.orElseThrow(() -> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(mentor.getNumberOfProjectsMentored() >= 3)
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		Project pro = new Project();
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		pro.setIdeaOwner(project.getIdeaOwner());
		pro.setMentor(mentor);
		pro.setProjectName(project.getProjectName());
		pro.setReleaseDate(project.getReleaseDate());
		projectRepository.save(pro);
		return pro.getProjectId();
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		List<Mentor> data = mentorRepository.findBynumberOfProjectsMentored(numberOfProjectsMentored);
		if(data.isEmpty())
			throw new InfyInternException("Service.MENTOR_NOT_FOUND");
		List<MentorDTO> mentors = new ArrayList<MentorDTO>();
		data.stream().forEach((m) -> {
			MentorDTO dto = new MentorDTO();
			dto.setMentorId(m.getMentorId());
			dto.setMentorName(m.getMentorName());
			dto.setNumberOfProjectsMentored(m.getNumberOfProjectsMentored());
			mentors.add(dto);
		});
		return mentors;
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		Mentor m = mentorRepository.findById(mentorId).
				orElseThrow(() -> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(m.getNumberOfProjectsMentored() >= 3)
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		Project p = projectRepository.findById(projectId).
				orElseThrow(() -> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		m.setNumberOfProjectsMentored(m.getNumberOfProjectsMentored()+1);
		p.setMentor(m);
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		System.out.println("del operation for projectId : "+projectId);
		Project p = projectRepository.findById(projectId).
				orElseThrow(() -> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		if(p.getMentor() != null) {
			Mentor m = p.getMentor();
			m.setNumberOfProjectsMentored(m.getNumberOfProjectsMentored()-1);
			p.setMentor(null);
		}
		projectRepository.delete(p);
	}
}