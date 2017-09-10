app.controller('editModalCtrl',function($scope,$mdDialog,$mdToast,$http,data){
	$scope.api = data;
	$scope.close = function(){
		$mdDialog.hide();
	};

	$scope.editNew = function(api){
		$http.post('/api/apiData/editApi',api).then(function(data){
			$mdDialog.hide();
			$scope.showSimpleToast("api Updated");

		});
	};

	$scope.showSimpleToast = function(msg) {
                   $mdToast.show(
                     $mdToast.simple()
                           .textContent(msg)
                           .action('OK')
                           .highlightAction(false)
                  );
    };
});