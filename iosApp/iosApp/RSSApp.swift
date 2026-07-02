//

import Foundation
import SwiftUI
import RssReader

@main
struct RSSApp: App {
    let rss: RssReader
    let store: ObservableFeedStore
    
    init() {
        KoinHelperKt.doInitKoin()
        let helper = KoinHelper()
        rss = helper.rssReader
        store = ObservableFeedStore(store: helper.feedStore)
    }
  
    var body: some Scene {
        WindowGroup {
            RootView().environmentObject(store)
        }
    }
}

class ObservableFeedStore: ObservableObject {
    @Published public var state: FeedState =  FeedState(progress: false, feeds: [], selectedFeed: nil)
    @Published public var sideEffect: FeedSideEffect?
    
    let store: FeedStore
    
    var stateWatcher : Closeable?
    var sideEffectWatcher : Closeable?

    init(store: FeedStore) {
        self.store = store
        stateWatcher = self.store.watchState().watch { [weak self] state in
            self?.state = state
        }
        sideEffectWatcher = self.store.watchSideEffect().watch { [weak self] state in
            self?.sideEffect = state
        }
    }
    
    public func dispatch(_ action: FeedAction) {
        store.dispatch(action: action)
    }
    
    deinit {
        stateWatcher?.close()
        sideEffectWatcher?.close()
    }
}

public typealias DispatchFunction = (FeedAction) -> ()

public protocol ConnectedView: View {
    associatedtype Props
    associatedtype V: View
    
    func map(state: FeedState, dispatch: @escaping DispatchFunction) -> Props
    func body(props: Props) -> V
}

public extension ConnectedView {
    func render(state: FeedState, dispatch: @escaping DispatchFunction) -> V {
        let props = map(state: state, dispatch: dispatch)
        return body(props: props)
    }
    
    var body: StoreConnector<V> {
        return StoreConnector(content: render)
    }
}

public struct StoreConnector<V: View>: View {
    @EnvironmentObject var store: ObservableFeedStore
    let content: (FeedState, @escaping DispatchFunction) -> V
    
    public var body: V {
        return content(store.state, store.dispatch)
    }
}

