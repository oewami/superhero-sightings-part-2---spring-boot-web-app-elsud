$(document).ready(function () {
    loadSuperpowers();
    loadSuperhero();
    updateSuperhero();
});

function loadSuperhero() {
    clearSuperheroDetails();
    var superheroId = GetParameterValue("id");
    $('#errorMessages').empty();
    var superheroRows = $('#superheroRows');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero/' + superheroId,
        success: function(data, status) {
            var row = '<tr>';
            row += '<td>name</td>';
            row += '<td>' + data.name + '</td>';
            row += '</tr>';
            superheroRows.append(row);
            row = '<tr><td>description</td><td>' + data.description + '</td>';
            superheroRows.append(row);
            superheroRows.append('<tr><td>superpower</td><td>' + data.superpower.id + '</td>');
            
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


function loadSuperpowers() {
    var superpowerEdit = $('#selectSuperpowerEdit');
    var superpowerAdd = $('#selectSuperpowerAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superpower',
        success: function(superpowerArray) {
            $.each(superpowerArray, function(index, superpower){
                var name = superpower.name;
                var superpowerId = superpower.id;
                var row = '<option value="' + superpowerId + '">' + name + '</option>'
                superpowerEdit.append(row);
                superpowerAdd.append(row); 
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

function clearSuperheroDetails() {
    $('#superheroRows').empty();
}

function showEditForm() {
    var superheroId = GetParameterValue("id");
    $('#errorMessages').empty();

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero/' + superheroId,
        success: function(data, status) {
            $('#editName').val(data.name);
            $('#editDescription').val(data.description);
            $('#editSuperpower').val(data.superpower.id);
            $('#editSuperheroId').val(data.id);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#superheroDetailsTable').hide();
    $('#editFormDiv').show();   
}

function hideEditForm() {
    $('#errorMessages').empty();
    
    $('#editName').val('');
    $('#editDescription').val('');
    $('#selectSuperpowerEdit').val('');

    $('#superheroDetailsTable').show();
    $('#editFormDiv').hide();
}

function updateSuperhero() {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperpower = $('#selectSuperpowerEdit option:selected').val();

        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/superhero/' + $('#editSuperheroId').val(),
            data: JSON.stringify({
                name: $('#editName').val(),
                description: $('#editDescription').val(),
                superpowerId: selectedSuperpower
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadSuperhero();
            },
            'error': function() {
                $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.')); 
            }
        })
    })
}    

function deleteSuperhero() {
    var superheroId = GetParameterValue("id");
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/superhero/' + superheroId,
        success: function() {
            window.location.href="superheros.html";
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