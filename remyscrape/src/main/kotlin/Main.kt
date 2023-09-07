import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.a
import it.skrape.selects.html5.li
import it.skrape.selects.html5.ul

const val URL_LIST = "https://remywiki.com/DanceDanceRevolution_A3_Full_Song_List"
const val URL_BASE = "https://remywiki.com"

fun main(args: Array<String>) {
    skrape(HttpFetcher) {
        request {
            url = URL_LIST
        }
        response {
            if (responseStatus.code >= 300) {
                throw Exception("Failed to fetch page")
            }
            val hrefs = mutableListOf<String>()
            htmlDocument {
                relaxed = true
                ul {
                    findAll {
                        forEach {
                            li {
                                findAll {
                                    forEach {
                                        it.a {
                                            withAttributeKey = "title"
                                            findFirst {
                                                eachHref.firstOrNull()?.let { href -> hrefs.add(href) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            hrefs.subList(0, 3).forEach {

            }
        }
    }
}