//

import Foundation
import SwiftUI
import Newzero

@main
struct RSSApp: App {
    let rss: FeedManager
    let store: ObservableArticleStore
    
    init() {
        DIHelperKt.doInitKoin()
        let helper = DIHelper()
        rss = helper.rssReader
        store = ObservableArticleStore(store: helper.feedStore)
    }
  
    var body: some Scene {
        WindowGroup {
            RootView().environmentObject(store)
        }
    }
}

class ObservableArticleStore: ObservableObject {
    @Published public var state: ArticleState =  ArticleState(progress: false, sources: [], selectedFeed: nil)
    @Published public var sideEffect: ArticleEffect?
    
    let store: ArticleStore
    
    var stateWatcher : Closeable?
    var sideEffectWatcher : Closeable?

    init(store: ArticleStore) {
        self.store = store
        stateWatcher = self.store.watchState().watch { [weak self] state in
            self?.state = state
        }
        sideEffectWatcher = self.store.watchSideEffect().watch { [weak self] state in
            self?.sideEffect = state
        }
    }
    
    public func dispatch(_ action: ArticleAction) {
        store.dispatch(action: action)
    }
    
    deinit {
        stateWatcher?.close()
        sideEffectWatcher?.close()
    }
}

public typealias DispatchFunction = (ArticleAction) -> ()

public protocol ConnectedView: View {
    associatedtype Props
    associatedtype V: View
    
    func map(state: ArticleState, dispatch: @escaping DispatchFunction) -> Props
    func body(props: Props) -> V
}

public extension ConnectedView {
    func render(state: ArticleState, dispatch: @escaping DispatchFunction) -> V {
        let props = map(state: state, dispatch: dispatch)
        return body(props: props)
    }
    
    var body: StoreConnector<V> {
        return StoreConnector(content: render)
    }
}

public struct StoreConnector<V: View>: View {
    @EnvironmentObject var store: ObservableArticleStore
    let content: (ArticleState, @escaping DispatchFunction) -> V
    
    public var body: V {
        return content(store.state, store.dispatch)
    }
}
