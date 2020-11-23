package com.example.atommovielisting.model

class ListEntry(val id: Int, val title: String, val poster_path: String, val overview: String, val page: Int, val popularity: Float, val vote_average: Float) {
}

// An extension to create a FeedEntry object from the server representation of an entry.
//extension FeedEntry {
//    convenience init(context: NSManagedObjectContext, serverEntry: ServerEntry, page: Int32) {
//        self.init(context: context)
//        self.id = serverEntry.id
//        self.title = serverEntry.title
//        self.poster_path = serverEntry.poster_path
//        self.overview = serverEntry.overview
//        self.release_date = getDate(serverEntry.release_date)
//        self.popularity = serverEntry.popularity ?? 0
//        self.original_language = original_language
//        self.vote_average = vote_average
//        self.page = page
//    }
//}