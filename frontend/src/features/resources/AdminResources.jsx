import { AdminLayout } from '../admin/AdminLayout';
import { Plus, Search, Trash2, BookOpen, MapPin } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import api from '../../api';

export function AdminResources() {
  const [activeTab, setActiveTab] = useState('books');
  const [showAddModal, setShowAddModal] = useState(false);
  const [resources, setResources] = useState([]);
  
  const initialFormState = {
    type: 'BOOK',
    name: '',
    author: '',
    isbn: '',
    category: '',
    capacity: '',
    location: ''
  };
  const [formData, setFormData] = useState(initialFormState);

  const fetchResources = useCallback(async () => {
    try {
      const type = activeTab === 'books' ? 'BOOK' : 'BOOTH';
      const response = await api.get(`/resources?type=${type}`);
      setResources(response.data);
    } catch (error) {
      console.error(error);
    }
  }, [activeTab]);

  useEffect(() => {
    fetchResources();
  }, [fetchResources]);

  // FIX: Added explicit validation and error handling
  const handleAddSubmit = async (e) => {
    e.preventDefault(); // Prevent accidental form submissions

    if (!formData.name.trim()) {
      alert("Please enter a name or title for the resource.");
      return;
    }

    try {
      await api.post('/resources', formData);
      alert("Resource successfully added!");
      setShowAddModal(false);
      setFormData(initialFormState); // Reset form
      
      // If we added a booth while on the booth tab, refresh the list
      if ((formData.type === 'BOOK' && activeTab === 'books') || 
          (formData.type === 'BOOTH' && activeTab === 'booths')) {
        fetchResources();
      }
    } catch (error) {
      console.error("Failed to add resource:", error);
      alert("Failed to save resource to the database. Please check connection.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this resource?")) return;
    
    try {
      await api.delete(`/resources/${id}`);
      fetchResources();
    } catch (error) {
      console.error(error);
      alert("Failed to delete resource.");
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Resource Management</h2>
            <p className="text-gray-600 mt-1">Manage library books and booths</p>
          </div>
          <button 
            onClick={() => {
              setFormData(initialFormState);
              setShowAddModal(true);
            }}
            className="flex items-center gap-2 bg-[#7F1D1D] text-white px-6 py-3 rounded-lg font-medium hover:bg-[#991B1B] transition-colors"
          >
            <Plus className="w-5 h-5" />
            Add Resource
          </button>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
          <div className="p-6 border-b border-gray-200">
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div className="flex gap-2">
                <button
                  onClick={() => setActiveTab('books')}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'books' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  <BookOpen className="w-5 h-5" />
                  Books
                </button>
                <button
                  onClick={() => setActiveTab('booths')}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'booths' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  <MapPin className="w-5 h-5" />
                  Booths
                </button>
              </div>

              <div className="flex gap-2">
                <div className="relative flex-1 md:w-64">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <input
                    type="text"
                    placeholder="Search resources..."
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D] focus:border-transparent"
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="p-6">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Name</th>
                    {activeTab === 'books' ? (
                      <>
                        <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Author</th>
                        <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">ISBN</th>
                      </>
                    ) : (
                      <>
                        <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Capacity</th>
                        <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Location</th>
                      </>
                    )}
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {resources.length === 0 && (
                    <tr>
                      <td colSpan="5" className="text-center py-8 text-gray-500">No resources found. Add one to get started.</td>
                    </tr>
                  )}
                  {resources.map((item) => (
                    <tr key={item.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-4 font-medium text-gray-900">{item.name}</td>
                      {activeTab === 'books' ? (
                        <>
                          <td className="px-4 py-4 text-gray-600">{item.author}</td>
                          <td className="px-4 py-4 text-gray-600 text-sm">{item.isbn}</td>
                        </>
                      ) : (
                        <>
                          <td className="px-4 py-4 text-gray-600">{item.capacity}</td>
                          <td className="px-4 py-4 text-gray-600">{item.location}</td>
                        </>
                      )}
                      <td className="px-4 py-4">
                        <span className={`px-2 py-1 rounded text-xs font-medium ${
                          item.available ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                        }`}>
                          {item.available ? 'Available' : 'Unavailable'}
                        </span>
                      </td>
                      <td className="px-4 py-4">
                        <div className="flex items-center gap-2">
                          <button onClick={() => handleDelete(item.id)} className="p-2 hover:bg-red-50 rounded-lg transition-colors" title="Delete">
                            <Trash2 className="w-4 h-4 text-red-600" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {showAddModal && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-xl font-bold text-gray-900">Add New Resource</h3>
              </div>
              <div className="p-6 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Resource Type</label>
                  <select 
                    value={formData.type}
                    onChange={(e) => setFormData({...formData, type: e.target.value})}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
                  >
                    <option value="BOOK">Book</option>
                    <option value="BOOTH">Study Booth</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Name / Title <span className="text-red-500">*</span></label>
                  <input 
                    type="text" 
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]" 
                    placeholder="Enter resource name"
                  />
                </div>

                {formData.type === 'BOOK' ? (
                  <>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Author</label>
                      <input 
                        type="text" 
                        value={formData.author}
                        onChange={(e) => setFormData({...formData, author: e.target.value})}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]" 
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">ISBN</label>
                      <input 
                        type="text" 
                        value={formData.isbn}
                        onChange={(e) => setFormData({...formData, isbn: e.target.value})}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]" 
                      />
                    </div>
                  </>
                ) : (
                  <>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Capacity</label>
                      <input 
                        type="text" 
                        value={formData.capacity}
                        onChange={(e) => setFormData({...formData, capacity: e.target.value})}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]" 
                        placeholder="e.g., 4 people"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Location/Floor</label>
                      <input 
                        type="text" 
                        value={formData.location}
                        onChange={(e) => setFormData({...formData, location: e.target.value})}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]" 
                        placeholder="e.g., 2nd Floor, Left Wing"
                      />
                    </div>
                  </>
                )}
              </div>
              <div className="p-6 border-t border-gray-200 flex gap-3">
                <button 
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 px-4 py-2 border-2 border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button 
                  onClick={handleAddSubmit}
                  className="flex-1 px-4 py-2 bg-[#7F1D1D] text-white rounded-lg font-medium hover:bg-[#991B1B] transition-colors"
                >
                  Add Resource
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}