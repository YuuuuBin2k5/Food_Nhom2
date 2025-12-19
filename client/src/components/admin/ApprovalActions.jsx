const ApprovalActions = ({ onApprove, onReject }) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-3">
      <div className="flex gap-2">
        <button
          onClick={onApprove}
          className="flex-1 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl font-bold hover:from-green-700 hover:to-emerald-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
          title="Ctrl+A"
        >
          <span className="text-lg">✓</span>
          <span>Duyệt</span>
        </button>
        <button
          onClick={onReject}
          className="flex-1 py-3 bg-gradient-to-r from-red-600 to-rose-600 text-white rounded-xl font-bold hover:from-red-700 hover:to-rose-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
          title="Ctrl+R"
        >
          <span className="text-lg">✕</span>
          <span>Từ chối</span>
        </button>
      </div>
      <div className="mt-2 p-2 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg text-xs text-gray-700 text-center border border-blue-100">
        ⌨️ <kbd className="px-1 py-0.5 bg-white rounded shadow-sm text-xs">Ctrl+A</kbd> Duyệt | <kbd className="px-1 py-0.5 bg-white rounded shadow-sm text-xs">Ctrl+R</kbd> Từ chối
      </div>
    </div>
  );
};

export default ApprovalActions;
