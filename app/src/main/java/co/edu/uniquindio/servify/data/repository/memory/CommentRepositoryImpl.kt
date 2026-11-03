package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor() : CommentRepository {

    private val _comments = MutableStateFlow(SampleData.commentsList)

    override fun observeCommentsForPublication(publicationId: String): Flow<List<Comment>> {
        return _comments.map { list ->
            list.filter { it.publicationId == publicationId }
        }
    }

    override suspend fun addComment(comment: Comment) {
        _comments.update { listOf(comment) + it }
    }
}
