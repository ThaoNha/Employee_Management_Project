package vn.com.tma.training.ProjectTraining.service.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.com.tma.training.ProjectTraining.dto.EmployeeDTO;
import vn.com.tma.training.ProjectTraining.entity.*;
import vn.com.tma.training.ProjectTraining.entity.entityDeleted.AdvanceDeletedEntity;
import vn.com.tma.training.ProjectTraining.entity.entityDeleted.EmployeeDeletedEntity;
import vn.com.tma.training.ProjectTraining.entity.entityDeleted.WorkingDeletedEntity;
import vn.com.tma.training.ProjectTraining.entity.entityUpdated.EmployeeUpdatedEntity;
import vn.com.tma.training.ProjectTraining.mapper.EmployeeMapper;
import vn.com.tma.training.ProjectTraining.mapper.TransferAdvance;
import vn.com.tma.training.ProjectTraining.mapper.TransferEmployee;
import vn.com.tma.training.ProjectTraining.mapper.TransferWorking;
import vn.com.tma.training.ProjectTraining.repository.*;
import vn.com.tma.training.ProjectTraining.repository.deleted.AdvanceDeletedRepository;
import vn.com.tma.training.ProjectTraining.repository.deleted.EmployeeDeletedRepository;
import vn.com.tma.training.ProjectTraining.repository.deleted.WorkingDeletedRepository;
import vn.com.tma.training.ProjectTraining.repository.updated.EmployeeUpdatedRepository;
import vn.com.tma.training.ProjectTraining.service.EmployeeService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRep;
    @Autowired
    private EmployeeMapper mapper;
    @Autowired
    private TeamRepository teamRep;
    @Autowired
    private TransferEmployee transferEmployee;
    @Autowired
    private TransferWorking transferWorking;
    @Autowired
    private TransferAdvance transferAdvance;
    @Autowired
    private EmployeeDeletedRepository employeeDeletedRepository;
    @Autowired
    private EmployeeUpdatedRepository employeeUpdatedRepository;
    @Autowired
    private WorkingDeletedRepository workingDeletedRepository;
    @Autowired
    private WorkingRepository workingRepository;
    @Autowired
    private AdvanceDeletedRepository advanceDeletedRepository;
    @Autowired
    private AdvanceRepository advanceRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Set<EmployeeDTO> listEmployee() {
        Set<EmployeeDTO> employeeDTOSet = new HashSet<>();
        employeeRep.findAll().forEach(employee -> {
            employeeDTOSet.add(mapper.toDTO(employee));
        });

        return employeeDTOSet;
    }

    @Override
    public Page<EmployeeDTO> getPage(Integer pageIndex) {
        Page<EmployeeEntity> page = employeeRep.findAll(PageRequest.of(pageIndex, 5, Sort.by("no")));
        return page.map(entity -> mapper.toDTO(entity));
    }


    @Override
    public Page<EmployeeDTO> listEmployeeByTeam(Integer teamID, Integer page) {
        TeamEntity entity = teamRep.findById(teamID).orElseThrow(() -> new IllegalArgumentException("Team_id: " + teamID + " is not found!"));

        Page<EmployeeEntity> pageEmployee = employeeRep.findAllByTeam(entity, PageRequest.of(page - 1, 5, Sort.by("no")));
        return pageEmployee.map(employeeEntity -> mapper.toDTO(employeeEntity));
    }

    @Override
    public List<EmployeeDTO> listEmployeeByTeam(Integer team_id) {
        TeamEntity teamEntity = teamRep.findById(team_id).orElseThrow(() -> new IllegalArgumentException("Team_id: " + team_id + " is not found!"));

        List<EmployeeEntity> pageEmployee = employeeRep.findAllByTeam(teamEntity);
        List<EmployeeDTO> dtos = new ArrayList<>();
        pageEmployee.forEach(entity -> dtos.add(mapper.toDTO(entity)));
        return dtos;
    }

    @Override
    public EmployeeDTO findEmployee(Integer id) {
        EmployeeEntity entity = employeeRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee_id: " + id + " is not found!"));
        return mapper.toDTO(entity);

    }

    @Override
    public Page<EmployeeDTO> findByName(String name, Integer page) {
        Page<EmployeeEntity> pageEmployee = employeeRep.findByFullNameContainingIgnoreCase(name, PageRequest.of(page - 1, 5, Sort.by("no")));
        return pageEmployee.map(employeeEntity -> mapper.toDTO(employeeEntity));
    }

    @Override
    public List<EmployeeDTO> findByName(String name) {
        List<EmployeeEntity> pageEmployee = employeeRep.findByFullNameContainingIgnoreCase(name);
        List<EmployeeDTO> dtos = new ArrayList<>();
        pageEmployee.forEach(entity -> dtos.add(mapper.toDTO(entity)));
        return dtos;
    }


    @Override
    public EmployeeDTO newEmployee(EmployeeDTO employeeDTO) {
        TeamEntity teamEntity = teamRep.findById(employeeDTO.getTeamID()).orElseThrow(() -> new IllegalArgumentException("Team is not found!"));
        EmployeeEntity entity = employeeRep.save(mapper.toEntity(employeeDTO, teamEntity));
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setEmployee(entity);
        imageRepository.save(imageEntity);
        return mapper.toDTO(entity);
    }

    @Override
    public EmployeeDTO updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        EmployeeEntity entity = employeeRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee_id: " + id + " is not found!"));

        EmployeeUpdatedEntity updatedEntity = transferEmployee.entityToUpdated(entity);
        entity.setFullName(employeeDTO.getFullName());
        entity.setAge(employeeDTO.getAge());
        entity.setStartDay(employeeDTO.getStartDay());
        TeamEntity teamEntity = teamRep.findById(employeeDTO.getTeamID()).orElseThrow(() -> new IllegalArgumentException("Team is not found!"));
        entity.setTeam(teamEntity);
        entity.setAddress(employeeDTO.getAddress());
        entity.setMoneyPerHour(employeeDTO.getMoneyPerHour());
        entity.setMale(employeeDTO.isMale());
        entity.setAddress(employeeDTO.getAddress());


        employeeUpdatedRepository.save(updatedEntity);
        return mapper.toDTO(employeeRep.save(entity));

    }

    @Override
    public void deleteEmployee(Integer id) {
        EmployeeEntity entity = employeeRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee_id: " + id + " is not found!"));
        ImageEntity imageEntity = imageRepository.findByEmployee(entity);
        EmployeeDeletedEntity deletedEntity = transferEmployee.entityToDeleted(entity);
        Iterable<WorkingEntity> workingEntitySet = workingRepository.findAllByEmployeeNo(id);
        Iterable<AdvanceEntity> advanceEntitySet = advanceRepository.findAllByEmployeeNo(id);
        if (workingEntitySet != null && advanceEntitySet != null) {
            workingEntitySet.forEach(workingEntity -> {
                WorkingDeletedEntity workingDeletedEntity = transferWorking.entityToDeleted(workingEntity);
                workingDeletedRepository.save(workingDeletedEntity);
                workingRepository.delete(workingEntity);
            });
            advanceEntitySet.forEach(advanceEntity -> {
                AdvanceDeletedEntity advanceDeletedEntity = transferAdvance.entityToDeleted(advanceEntity);
                advanceDeletedRepository.save(advanceDeletedEntity);
                advanceRepository.delete(advanceEntity);
            });
            imageRepository.delete(imageEntity);
            employeeDeletedRepository.save(deletedEntity);
            employeeRep.delete(entity);

        } else {
            throw new IllegalArgumentException("Delete is not complete!");
        }

    }

    @Override
    public String deleteAll(List<Integer> ids) {
        StringBuilder result = new StringBuilder();
        for (Integer id : ids) {
            try {
                deleteEmployee(id);

            } catch (Exception e) {
                result.append(e.getMessage()).append("-");
            }
        }
        return result.toString().toString();
    }


}
