$(function() {
    new myScalaJs().displayUserNumber()
	$('a.delete').click(function(e) {
		if(confirm('Are you sure to delete this?')) {
			var href = $(this).attr('href')
			console.log(href)
			$.ajax({
				type: 'DELETE',
				url: href,
				success: function() {
					document.location.reload()
				}
			})
		}
		e.preventDefault();
		return false
	})

})