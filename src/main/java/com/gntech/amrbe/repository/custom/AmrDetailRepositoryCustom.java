package com.gntech.amrbe.repository.custom;

import com.gntech.amrbe.dto.FormResult;

public interface AmrDetailRepositoryCustom {
    FormResult getAmrDetail(String username, Long treeId);
}
