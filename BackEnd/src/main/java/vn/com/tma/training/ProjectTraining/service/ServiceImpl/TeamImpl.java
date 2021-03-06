package vn.com.tma.training.ProjectTraining.service.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.com.tma.training.ProjectTraining.dto.TeamDTO;
import vn.com.tma.training.ProjectTraining.entity.EmployeeEntity;
import vn.com.tma.training.ProjectTraining.entity.TeamEntity;
import vn.com.tma.training.ProjectTraining.mapper.TeamMapper;
import vn.com.tma.training.ProjectTraining.repository.EmployeeRepository;
import vn.com.tma.training.ProjectTraining.repository.TeamRepository;
import vn.com.tma.training.ProjectTraining.service.EmployeeService;
import vn.com.tma.training.ProjectTraining.service.TeamService;

import java.util.ArrayList;
import java.util.List;

@Service

public class TeamImpl implements TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMapper mapper;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public Page<TeamDTO> listTeam(Integer page) {
        Page<TeamEntity> pageTeam = teamRepository.findAll(PageRequest.of(page - 1, 5, Sort.by("no")));
        return pageTeam.map(team -> mapper.toDTO(team));
    }

    @Override
    public TeamDTO findById(Integer id) {
        TeamEntity entity = teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Team is not found!"));
        return mapper.toDTO(entity);
    }

    @Override
    public Page<TeamDTO> findTeamByName(String name, Integer page) {
        Page<TeamEntity> pageTeam = teamRepository.findByTeamName(name, PageRequest.of(page - 1, 5, Sort.by("no")));
        return pageTeam.map(teamEntity -> mapper.toDTO(teamEntity));
    }

    @Override
    public TeamDTO addTeam(TeamDTO teamDTO) {
        List<TeamEntity> name = teamRepository.findByName(teamDTO.getName().trim());
        if (!(name.size() > 0)) {
            teamDTO.setName(teamDTO.getName().trim());
            TeamEntity teamEntity = teamRepository.save(mapper.toEntity(teamDTO));
            return mapper.toDTO(teamEntity);
        } else {
            throw new IllegalArgumentException("Name Team used! ");
        }

    }

    @Override
    public TeamDTO updateTeam(Integer id, TeamDTO teamDTO) {

        TeamEntity entity = teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Team is not found!"));
        entity.setName(teamDTO.getName());
        return mapper.toDTO(teamRepository.save(entity));


    }

    @Override
    public void delete(Integer id) {

        TeamEntity entity = teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Team is not found!"));
        List<EmployeeEntity> employeeEntities=employeeRepository.findAllByTeam(entity);
        for (EmployeeEntity employeeEntity:employeeEntities) {
            employeeService.deleteEmployee(employeeEntity.getNo());
        }
        teamRepository.delete(entity);
    }

    @Override
    public List<TeamDTO> listTeam() {
        List<TeamDTO> dtos = new ArrayList<>();
        Iterable<TeamEntity> pageTeam = teamRepository.findAll();
        pageTeam.forEach(teamEntity ->
                dtos.add(mapper.toDTO(teamEntity))
        );
        return dtos;

    }


}
