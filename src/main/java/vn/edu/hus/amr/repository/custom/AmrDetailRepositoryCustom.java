package vn.edu.hus.amr.repository.custom;

import vn.edu.hus.amr.dto.FormResult;

public interface AmrDetailRepositoryCustom {
    FormResult getAmrDetail(String username, Long treeId);

    FormResult getAmrDetailForExport(String paragraphPositions);
}
