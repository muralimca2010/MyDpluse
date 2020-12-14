package com.rs.mydpluse

import java.io.Serializable


data class ShowsDetailResponse(

    val id: String? = null,
    val url: String? = null,
    val name: String? = null,
    val showCategory: String? = null,
    val availableSeasons: String? = null,
    val showFirstAiredDate: String? = null,
    val seasons: MutableList<Seasons>? = null,
    val talents: MutableList<Talents>? = null
)

data class Seasons (
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val seasonNumber: String? = null,
    val episodes: MutableList<Episodes>? = null

    ): Serializable {

    override fun toString(): String {
        return "Seasons{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", seasonNumber='" + seasonNumber + '\'' +
                ", episodes='" + episodes + '\'' +
                '}'

    }
}



data class Talents(
    val name: String? = null,
    val talentPhoto: TalentPhoto? = null,
    val bioBlurb: String? = null

    )

data class Episodes(
    val id: String? = null,
    val title: String? = null,
    val episodeNumber: String? = null,
    val images: Images? = null,
    val duration: String? = null,
    val videoPreview: VideoPreview? = null,
    val durationWatched: String? = null,
    val videoPlayback: VideoPlayback? = null

    ) : Serializable {

    override fun toString(): String {
        return "Episodes{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", episodeNumber='" + episodeNumber + '\'' +
                ", images='" + images + '\'' +
                ", duration='" + duration + '\'' +
                ", videoPreview='" + videoPreview + '\'' +
                ", durationWatched='" + durationWatched + '\'' +
                ", videoPlayback='" + videoPlayback + '\'' +
                '}'

    }
}

data class Images(
    val hero: Hero? = null,
    val vertical: Vertical? = null,
    val horizontal: Horizontal? = null

    ): Serializable {

    override fun toString(): String {
        return "Images{" +
                "hero=" + hero +
                ", vertical='" + vertical + '\'' +
                ", horizontal='" + horizontal + '\'' +
                '}'

    }
}

data class Hero(
    val url: String? = null
)

data class Vertical(
    val url: String? = null
)

data class Horizontal(
    val url: String? = null
)

data class VideoPreview(
    val url: String? = null

    )

data class VideoPlayback(
    val url: String? = null

    )

data class TalentPhoto(
    val url: String? = null
)



