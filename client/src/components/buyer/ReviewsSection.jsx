import { useState, useEffect } from 'react';
import StarRating from '../common/StarRating';
import { showToast } from '../../utils/toast';
import { formatDateShort } from '../../utils/format';

function ReviewsSection({ productId }) {
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showAddReview, setShowAddReview] = useState(false);
    const [newReview, setNewReview] = useState({
        rating: 5,
        comment: ''
    });

    useEffect(() => {
        loadReviews();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [productId]);

    const loadReviews = async () => {
        try {
            // TODO: Call API to get reviews
            // const response = await api.get(`/reviews/product/${productId}`);
            // setReviews(response.data);
            
            // For now, show empty reviews
            setReviews([]);
        } catch (error) {
            console.error('Error loading reviews:', error);
            setReviews([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitReview = async (e) => {
        e.preventDefault();

        if (!newReview.comment.trim()) {
            showToast.warning('Vui lòng nhập nhận xét');
            return;
        }

        try {
            // TODO: Call API to submit review
            showToast.success('Đã thêm đánh giá thành công!');
            setNewReview({ rating: 5, comment: '' });
            setShowAddReview(false);
            loadReviews();
        } catch (error) {
            showToast.error('Lỗi khi thêm đánh giá');
        }
    };

    const calculateAverageRating = () => {
        if (reviews.length === 0) return 0;
        const sum = reviews.reduce((acc, review) => acc + review.rating, 0);
        return (sum / reviews.length).toFixed(1);
    };

    if (loading) {
        return (
            <div className="bg-white rounded-2xl p-8 mt-8 shadow-sm text-center">
                <div className="w-8 h-8 border-3 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
                <p className="text-gray-500 mt-4">Đang tải đánh giá...</p>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-2xl p-8 mt-8 shadow-sm">
            {/* Header */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8 pb-6 border-b">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-2">💬 Đánh giá sản phẩm</h2>
                    {reviews.length > 0 && (
                        <div className="flex items-center gap-3">
                            <span className="text-4xl font-bold text-blue-600">{calculateAverageRating()}</span>
                            <div>
                                <StarRating rating={Math.round(parseFloat(calculateAverageRating()))} size="large" />
                                <p className="text-sm text-gray-500 mt-1">{reviews.length} đánh giá</p>
                            </div>
                        </div>
                    )}
                </div>
                <button
                    onClick={() => setShowAddReview(!showAddReview)}
                    className={`px-6 py-3 rounded-xl font-semibold transition-all flex items-center gap-2 ${
                        showAddReview
                            ? 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            : 'bg-gradient-to-r from-blue-500 to-indigo-500 text-white hover:from-blue-600 hover:to-indigo-600 shadow-lg hover:shadow-xl'
                    }`}
                >
                    {showAddReview ? (
                        <>
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                            Hủy
                        </>
                    ) : (
                        <>
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                            Viết đánh giá
                        </>
                    )}
                </button>
            </div>

            {/* Add Review Form */}
            {showAddReview && (
                <form onSubmit={handleSubmitReview} className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 mb-8 border border-blue-100">
                    <div className="mb-6">
                        <label className="block font-semibold text-gray-700 mb-3">Đánh giá của bạn</label>
                        <StarRating
                            rating={newReview.rating}
                            size="large"
                            interactive={true}
                            onRatingChange={(rating) => setNewReview({ ...newReview, rating })}
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block font-semibold text-gray-700 mb-3">Nhận xét</label>
                        <textarea
                            value={newReview.comment}
                            onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                            placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm..."
                            rows="4"
                            className="w-full p-4 border border-gray-200 rounded-xl text-gray-800 bg-white placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                        />
                    </div>

                    <button
                        type="submit"
                        className="px-8 py-3 bg-gradient-to-r from-blue-500 to-indigo-500 text-white font-semibold rounded-xl hover:from-blue-600 hover:to-indigo-600 transition-all shadow-lg hover:shadow-xl"
                    >
                        Gửi đánh giá
                    </button>
                </form>
            )}

            {/* Reviews List */}
            <div className="space-y-6">
                {reviews.length === 0 ? (
                    <div className="text-center py-12">
                        <span className="text-5xl block mb-4">💭</span>
                        <p className="text-gray-500">Chưa có đánh giá nào. Hãy là người đầu tiên!</p>
                    </div>
                ) : (
                    reviews.map(review => (
                        <div key={review.reviewId} className="p-6 bg-gray-50 rounded-2xl hover:bg-gray-100 transition-colors">
                            <div className="flex items-start justify-between gap-4 mb-3">
                                <div className="flex items-center gap-3">
                                    <div className="w-10 h-10 bg-gradient-to-br from-blue-400 to-indigo-400 rounded-full flex items-center justify-center text-white font-bold">
                                        {(review.buyer?.fullName || 'U')[0]}
                                    </div>
                                    <div>
                                        <p className="font-semibold text-gray-800">{review.buyer?.fullName || 'Người dùng'}</p>
                                        <p className="text-sm text-gray-500">{formatDateShort(review.reviewDate)}</p>
                                    </div>
                                </div>
                                <StarRating rating={review.rating} size="small" />
                            </div>
                            <p className="text-gray-700 leading-relaxed pl-13">{review.comment}</p>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default ReviewsSection;
