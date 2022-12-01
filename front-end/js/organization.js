$(document).ready(function () {
    loadSuperheros();
    loadOrganization();
    updateOrganization();
});

function loadOrganization() {
    clearOrganizationDetails();
    var organizationId = GetParameterValue("id");
    $('#errorMessages').empty();
    var organizationRows = $('#organizationRows');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/organization/' + organizationId,
        success: function(data, status) {
            var row = '<tr>';
            row += '<td>name</td>';
            row += '<td>' + data.name + '</td>';
            row += '</tr>';
            organizationRows.append(row);
            row = '<tr><td>description</td><td>' + data.description + '</td>';
            organizationRows.append(row);
            organizationRows.append('<tr><td>address</td><td>' + data.address + '</td>');
            organizationRows.append('<tr><td>members:</td><td></td>');
            loadOrganizationSuperheros(organizationId);
            
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

function loadOrganizationSuperheros(organizationId) {
    var organizationRows = $('#organizationRows');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero/forOrganization/' + organizationId,
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                organizationRows.append('<tr><td></td><td>' + superhero.name + '</td>');
               
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

function loadSuperheros() {
    var membersEdit = $('#selectMembersEdit');
    var membersAdd = $('#selectMembersAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero',
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                var name = superhero.name;
                var superheroId = superhero.id;
                var row = '<option value="' + superheroId + '">' + name + '</option>'
                membersEdit.append(row);
                membersAdd.append(row); 
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

function clearOrganizationDetails() {
    $('#organizationRows').empty();
}

function showEditForm() {
    var organizationId = GetParameterValue("id");
    $('#errorMessages').empty();

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/organization/' + organizationId,
        success: function(data, status) {
            $('#editName').val(data.name);
            $('#editDescription').val(data.description);
            $('#editAddress').val(data.address);
            $('#editOrganizationId').val(data.id);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#organizationDetailsTable').hide();
    $('#editFormDiv').show();   
}

function hideEditForm() {
    $('#errorMessages').empty();
    
    $('#editName').val('');
    $('#editDescription').val('');
    $('#editAddress').val('');
    $('#selectMembersEdit').val('');

    $('#organizationDetailsTable').show();
    $('#editFormDiv').hide();
}

function updateOrganization(organizationId) {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedMembers = [];
        $.each($('#selectMembersEdit option:selected'), function(){
            selectedMembers.push($(this).val());
        });
        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/organization/' + $('#editOrganizationId').val(),
            data: JSON.stringify({
                organizationId: $('#editOrganizationId').val(),
                name: $('#editName').val(),
                description: $('#editDescription').val(),
                address: $('#editAddress').val(),
                members: selectedMembers
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadOrganization();
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

function deleteOrganization() {
    var organizationId = GetParameterValue("id");
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/organization/' + organizationId,
        success: function() {
            window.location.href="organizations.html";
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