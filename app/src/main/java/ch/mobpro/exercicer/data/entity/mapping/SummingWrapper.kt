package ch.mobpro.exercicer.data.entity.mapping

data class SummingWrapper(val sumSeconds: Int = 0, val sumMeters: Int = 0) {
    operator fun plus(addition: SummingWrapper): SummingWrapper {
        return SummingWrapper(
            this.sumSeconds + addition.sumSeconds,
            this.sumMeters + addition.sumMeters
        )
    }

    operator fun plus(addition: TrainingSportTrainingTypeMapping): SummingWrapper {
        val trainingTime = ((addition.training.trainingTimeHour ?: 0) * 60 * 60) +
                ((addition.training.trainingTimeMinutes ?: 0) * 60) +
                (addition.training.trainingTimeSeconds ?: 0)

        return SummingWrapper(
            this.sumSeconds + trainingTime,
            this.sumMeters + (addition.training.trainingDistanceInMeters ?: 0)
        )
    }
}
