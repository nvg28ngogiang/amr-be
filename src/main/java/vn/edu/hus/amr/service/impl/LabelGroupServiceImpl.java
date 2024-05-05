package vn.edu.hus.amr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hus.amr.model.LabelGroup;
import vn.edu.hus.amr.repository.LabelGroupRepository;
import vn.edu.hus.amr.service.LabelGroupService;

import java.util.List;

@Slf4j

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LabelGroupServiceImpl implements LabelGroupService {

    private final LabelGroupRepository labelGroupRepository;

    @Override
    public List<LabelGroup> findAll() {
        return labelGroupRepository.findAll();
    }

}
