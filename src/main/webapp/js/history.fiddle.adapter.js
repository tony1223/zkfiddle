if (History.emulated.pushState) {
	/*
	 * Provide Skeleton for HTML4 Browsers
	 */
	// Prepare
	History.pushState = History.replaceState = function(data, title, url, queue) {
		self.location.href = url;
	};
}else{
	zk.afterMount(function(){
		zAu.send(new zk.Event(zk.Desktop._dt, "onPushStateInit"), -1);
	});
	
	History.Adapter.bind(window,'statechange',function(){ 
		// Note: We are using statechange instead of popstate
		
		if (!History.pushed ) {
			self.location.href = History.getState().url; 
		} 
		History.pushed = false;
			/*
			var State = History.getState(); // Note: We are using History.getState() instead of event.state
			zAu.send(new zk.Event(zk.Desktop._dt, "onStateChange", {
				data: State.data,
				title: State.title,
				url: State.hash
			}), 0);
			*/
	});
}
