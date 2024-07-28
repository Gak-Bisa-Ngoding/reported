package com.rleonb.reported.domain.models

enum class ReportedTheme {
    FollowSystem,
    Light,
    Dark;

    companion object {
        fun default(): ReportedTheme {
            return FollowSystem
        }
    }
}