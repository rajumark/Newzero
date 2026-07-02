//

import SwiftUI
import Newzero

struct FeedsList: ConnectedView {
    
    struct Props {
        let defaultFeeds: [RssFeed]
        let userFeeds: [RssFeed]
        let onAdd: (String) -> ()
        let onRemove: (String) -> ()
    }
    
    func map(state: ArticleState, dispatch: @escaping DispatchFunction) -> Props {
        return Props(defaultFeeds: state.sources.filter { $0.isPreloaded },
                     userFeeds: state.sources.filter { !$0.isPreloaded },
                     onAdd: { url in
                        dispatch(ArticleAction.Add(url: url))
                     }, onRemove: { url in
                        dispatch(ArticleAction.Delete(url: url))
                     })
    }
    
    @SwiftUI.State var showsAlert: Bool = false
    
    func body(props: Props) -> some View {
        List {
            ForEach(props.defaultFeeds) { FeedRow(feed: $0) }
            ForEach(props.userFeeds) { FeedRow(feed: $0) }
                .onDelete( perform: { set in
                    set.map { props.userFeeds[$0] }.forEach { props.onRemove($0.feedUrl) }
                })
        }
        .alert(isPresented: $showsAlert, TextAlert(title: "Title") {
            if let url = $0 {
                props.onAdd(url)
            }
        })
        .navigationTitle("Feeds list")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarItems(trailing: Button(action: {
            showsAlert = true
        }) {
            Image(systemName: "plus.circle").imageScale(.large)
        })
    }
}

extension RssFeed: Identifiable { }
