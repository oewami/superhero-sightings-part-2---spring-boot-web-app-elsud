$(document).ready(function () {
    loadSuperheros();
    loadLocations();
    loadSighting();;
    updateSighting();
});

function loadSighting() {
    clearSightingDetails();
    var sightingId = GetParameterValue("id");
    $('#errorMessages').empty();
    var sightingRows = $('#sightingRows');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/sighting/' + sightingId,
        success: function(data, status) {
            sightingRows.append('<tr><td>superhero</td><td><a href="superhero.html?id=' + data.superhero.id +'">' + data.superhero.name + '</a></td>');
            sightingRows.append('<tr><td>location</td><td>' + data.location.name + '</td>');
            sightingRows.append('<tr><td>date</td><td>' + data.date + '</td>');
            sightingRows.append('<tr><td>latitude</td><td>' + data.location.latitude + '</td>');
            sightingRows.append('<tr><td>longitude</td><td>' + data.location.longitude + '</td>');  
        },
        error: function() {
            $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.'));
        }
    })
}

function GetParameterValue(param) {
    var url = document.location.href.slice(document.location.href.indexOf('?') + 1).split('&');
    for (var i=0; i<url.length; i++) {
        var urlparam = url[i].split('=');
    }
    if (urlparam[0] == param) {
        return urlparam[1];
    }
}

function loadSuperheros() {
    var superherosEdit = $('#selectSuperheroEdit');
    var superherosAdd = $('#selectSuperheroAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero',
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                var name = superhero.name;
                var superheroId = superhero.id;
                var row = '<option value="' + superheroId + '">' + name + '</option>'
                superherosEdit.append(row);
                superherosAdd.append(row);   
            })
        
        },
        error: function() {
            $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.'));
        }
    }); 
}

function loadLocations() {
    var locationsEdit = $('#selectLocationEdit');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/location',
        success: function(locationArray) {
            $.each(locationArray, function(index, location){
                var name = location.name;
                var locationId = location.id;
                var row = '<option value="' + locationId + '">' + name + '</option>'
                locationsEdit.append(row);
            })
        
        },
        error: function() {
            $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.'));
        }
    }); 
}

function clearSightingDetails() {
    $('#sightingRows').empty();
}

function showEditForm() {
    var sightingId = GetParameterValue("id");
    $('#errorMessages').empty();
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/sighting/' + sightingId,
        success: function(data, status) {
            var date = data.date.split("-");
            $('#editDate').val(data.date);
            $('#editSuperhero').val(data.superhero.id);
            $('#editLocation').val(data.location.id);
            $('#editSightingId').val(sightingId);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#sightingDetailsTable').hide();
    $('#editFormDiv').show();
}


function hideEditForm() {
    $('#errorMessages').empty();
    
    $('#editDate').val('');
    $('#selectSuperheroEdit').val('');
    $('#selectLocationEdit').val('');

    $('#sightingDetailsTable').show();
    $('#editFormDiv').hide();
}

function updateSighting() {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperhero = $('#selectSuperheroEdit option:selected').val();
        var selectedLocation = $('#selectLocationEdit option:selected').val();
        var sightingId = GetParameterValue("id");
        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/sighting/' + sightingId,
            data: JSON.stringify({
                date: $('#editDate').val(),
                superheroId: selectedSuperhero,
                locationId: selectedLocation
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadSightings();
            },
            'error': function() {
                $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Sighting must be unique. Date cannot be in the future.')); 
            }
        })
    })
}

function deleteSighting() {
    var sightingId = GetParameterValue("id");
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/sighting/' + sightingId,
        success: function() {
            window.location.href="sightings.html";
        }
    });
}

function checkAndDisplayValidationErrors(input) {
    $('#errorMessages').empty();
    
    var errorMessages = [];
    
    input.each(function() {
        if (!this.validity.valid) {
            var errorField = $('label[for=' + this.id + ']').text();
            errorMessages.push(errorField + ' ' + this.validationMessage);
        }  
    });
    
    if (errorMessages.length > 0){
        $.each(errorMessages,function(index,message) {
            $('#errorMessages').append($('<li>').attr({class: 'list-group-item list-group-item-danger'}).text(message));
        });
        // return true, indicating that there were errors
        return true;
    } else {
        // return false, indicating that there were no errors
        return false;
    }
}
