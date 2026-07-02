package com.rajumark.newzero.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("rss", "", "")
data class ArticleFeed(
    val version: String?,
    var feedUrl: String = "",
    var isPreloaded: Boolean = false,
    @XmlSerialName("channel", "", "") val channel: FeedChannel?
)

@Serializable
data class FeedChannel(
    @XmlElement val title: String?,
    @XmlElement val description: String?,
    @XmlElement val link: String?,
    @XmlElement val copyright: String? = null,
    @XmlSerialName("item", "", "") @XmlElement val item: List<ArticleItem>,
    @XmlSerialName("image", "", "") val image: FeedImage? = null,

)

@Serializable
data class FeedImage(
    @XmlElement val url: String?,
    @XmlElement val title: String?,
    @XmlElement val link: String?,
    @XmlElement val width: Int?,
    @XmlElement val height: Int?
)

@Serializable
data class ArticleItem(
    @XmlElement val title: String?,
    @XmlElement val pubDate: String?,
    @XmlElement val link: String?,
    @XmlElement val guid: String,
    @XmlElement val description: String?,
    @XmlSerialName(
        "encoded",
        "http://purl.org/rss/1.0/modules/content/",
        "content"
    ) @XmlElement val contentEncoded: String?,
    @XmlSerialName(
        "enclosure",
        "http://search.yahoo.com/mrss/",
        "media"
    ) @XmlElement val mediaContent: ArticleMedia? = null
)

fun ArticleItem.extractImage(): String? {
    return mediaContent?.url ?: contentEncoded?.let { content ->
        val imgRegex = "<img[^>]+src=\"([^\"]+)\"".toRegex()
        return imgRegex.find(content)?.groupValues?.get(1)
    }
}

@Serializable
data class ArticleMedia(
    @XmlElement val type: String? = null,
    @XmlElement val url: String? = null,
    @XmlElement val height: String? = null,
    @XmlElement val width: String? = null,
    @XmlSerialName(
        "title",
        "http://search.yahoo.com/mrss/",
        "media"
    ) @XmlElement val mediaTitle: String? = null,
    @XmlSerialName(
        "description",
        "http://search.yahoo.com/mrss/",
        "media"
    ) val mediaDescription: MediaDesc? = null,
    @XmlSerialName(
        "credit",
        "http://search.yahoo.com/mrss/",
        "media"
    ) val mediaCredit: MediaCreditInfo? = null
)

@Serializable
@XmlSerialName("description", "http://search.yahoo.com/mrss/", "media")
data class MediaDesc(
    val type: String? = null,
    @XmlValue val value: String = ""
)

@Serializable
@XmlSerialName("credit", "http://search.yahoo.com/mrss/", "media")
data class MediaCreditInfo(
    val role: String? = null,
    val scheme: String? = null,
    @XmlValue val value: String = ""
)

@Serializable
@XmlSerialName("feed", "http://www.w3.org/2005/Atom", "")
data class AtomFeed(
    @XmlElement val title: String?,
    @XmlElement val subtitle: String? = null,
    @XmlElement val link: List<AtomLink>? = null,
    @XmlElement val updated: String? = null,
    @XmlSerialName("entry", "http://www.w3.org/2005/Atom", "") @XmlElement val entries: List<AtomEntry> = emptyList()
)

@Serializable
@XmlSerialName("link", "http://www.w3.org/2005/Atom", "")
data class AtomLink(
    val href: String?,
    val rel: String? = null,
    val type: String? = null
)

@Serializable
@XmlSerialName("entry", "http://www.w3.org/2005/Atom", "")
data class AtomEntry(
    @XmlElement val id: String?,
    @XmlElement val title: String?,
    @XmlElement val published: String?,
    @XmlElement val updated: String? = null,
    @XmlElement val content: AtomContent? = null,
    @XmlElement val summary: String? = null,
    @XmlElement val link: List<AtomLink>? = null,
    @XmlElement val author: List<AtomAuthor>? = null,
    @XmlSerialName("thumbnail", "http://search.yahoo.com/mrss/", "media") val mediaThumbnail: AtomMediaThumbnail? = null
)

@Serializable
@XmlSerialName("content", "http://www.w3.org/2005/Atom", "")
data class AtomContent(
    val type: String? = null,
    @XmlValue val value: String = ""
)

@Serializable
@XmlSerialName("author", "http://www.w3.org/2005/Atom", "")
data class AtomAuthor(
    @XmlElement val name: String?,
    @XmlElement val uri: String? = null,
    @XmlElement val email: String? = null
)

@Serializable
@XmlSerialName("thumbnail", "http://search.yahoo.com/mrss/", "media")
data class AtomMediaThumbnail(
    val url: String? = null,
    val height: String? = null,
    val width: String? = null
)
