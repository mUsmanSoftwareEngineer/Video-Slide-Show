package com.acatapps.videomaker.utils

import com.acatapps.videomaker.data.LookupData

object LookupUtils {

    fun getLookupDataList():ArrayList<LookupData> {
        return ArrayList<LookupData>().apply {
            for(type in LookupType.values()) {
                add(LookupData(type, type.toString()))
            }
        }
    }

    enum class LookupType {
        NONE,
        A1,A2,A3,A4,A5,A6,A7,A8,A9,
        B1,B2,B3,B4,B5,B6,B7,B8,B9,
        C1,C2,C3,C4,C5,C6,C7,C8,C9,
        D1,D2,D3,D4,D5,D6,D7,D8,D9,
        E1,E2,E3,E4,E5,E6,E7,E8,E9,
        F1
    }
}