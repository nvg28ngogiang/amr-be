package vn.edu.hus.amr.repository.custom;

import vn.edu.hus.amr.dto.FormResult;
import vn.edu.hus.amr.model.AppUserRole;

import java.util.List;

public interface AmrDetailRepositoryCustom {
    FormResult getAmrDetail(String username, Long treeId);

    FormResult getAmrDetailForExport(Long exportUserid);

    FormResult statisticUsers();
}
