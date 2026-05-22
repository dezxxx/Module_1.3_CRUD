package com.dezxxx.hometasks.crud.repository.impl.jdbc;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcPostRepositoryImpl implements PostRepository {

    private final Connection connection;

    public JdbcPostRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Post save(Post post) {

        String sql = """
                INSERT INTO post
                (title, content, created, updated, status,  writer_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(post.getCreated())
            );

            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(post.getUpdated())
            );

            ps.setString(5, post.getStatus());

            ps.setLong(6, post.getWriter().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    post.setId(rs.getLong(1));
                }
            }

            savePostLabels(post);

            return post;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error saving post",
                    e
            );
        }
    }

    private static final String BASE_SQL = """
                SELECT
                    p.id            AS post_id,
                    p.title         AS post_title,
                    p.content       AS post_content,
                    p.created       AS post_created,
                    p.updated       AS post_updated,
                    p.status        AS post_status,

                    w.id            AS writer_id,
                    w.first_name    AS writer_first_name,
                    w.last_name     AS writer_last_name,

                    l.id            AS label_id,
                    l.name          AS label_name

                FROM post p

                LEFT JOIN writer w
                    ON p.writer_id = w.id

                LEFT JOIN post_label pl
                    ON p.id = pl.post_id

                LEFT JOIN label l
                    ON l.id = pl.label_id
            """;

    @Override
    public List<Post> findAll() {

        String sql = BASE_SQL + "WHERE p.status != 'DELETED' ORDER BY p.id";

        try (PreparedStatement ps =
                connection.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()) {

            return mapPosts(rs);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error loading posts",
                    e
            );
        }
    }

    @Override
    public Optional<Post> findById(Long id) {

        String sql = BASE_SQL + "WHERE p.id = ? ORDER BY p.id";

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                return mapPosts(rs).stream().findFirst();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error loading post by id",
                    e
            );
        }
    }

    private List<Post> mapPosts(ResultSet rs) throws SQLException {

        Map<Long, Post> postMap = new LinkedHashMap<>();

        while (rs.next()) {

            Long postId = rs.getLong("post_id");

            Post post = postMap.get(postId);

            if (post == null) {

                post = new Post();
                post.setId(postId);
                post.setTitle(rs.getString("post_title"));
                post.setContent(rs.getString("post_content"));
                post.setStatus(rs.getString("post_status"));
                post.setLabels(new ArrayList<>());

                Writer writer = new Writer();
                writer.setId(rs.getLong("writer_id"));
                writer.setFirstName(rs.getString("writer_first_name"));
                writer.setLastName(rs.getString("writer_last_name"));
                post.setWriter(writer);

                Timestamp created = rs.getTimestamp("post_created");
                if (created != null) {
                    post.setCreated(created.toLocalDateTime());
                }

                Timestamp updated = rs.getTimestamp("post_updated");
                if (updated != null) {
                    post.setUpdated(updated.toLocalDateTime());
                }

                postMap.put(postId, post);
            }

            Long labelId = rs.getLong("label_id");

            if (!rs.wasNull()) {

                final Long lid = labelId;
                boolean alreadyExists = post.getLabels()
                        .stream()
                        .anyMatch(l -> l.getId().equals(lid));

                if (!alreadyExists) {

                    Label label = new Label();
                    label.setId(labelId);
                    label.setName(rs.getString("label_name"));

                    post.getLabels().add(label);
                }
            }
        }

        return new ArrayList<>(postMap.values());
    }

    @Override
    public Post update(Post post) {

        String sql = """
                UPDATE post
                SET title = ?,
                    content = ?,
                    updated = ?,
                    status = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setString(1, post.getTitle());

            ps.setString(2, post.getContent());

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(LocalDateTime.now())
            );

            ps.setString(4, post.getStatus());

            ps.setLong(5, post.getId());

            ps.executeUpdate();

            deletePostLabels(post.getId());

            savePostLabels(post);

            return post;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error updating post",
                    e
            );
        }
    }

    @Override
    public void deleteById(Long id) {

        String sql = """
                UPDATE post
                SET status  = 'DELETED',
                    updated = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error soft-deleting post",
                    e
            );
        }
    }

    private void savePostLabels(Post post)
            throws SQLException {

        String sql = """
                INSERT INTO post_label
                (post_id, label_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            for (Label label : post.getLabels()) {

                ps.setLong(1, post.getId());

                ps.setLong(2, label.getId());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void deletePostLabels(Long postId)
            throws SQLException {

        String sql = """
                DELETE FROM post_label
                WHERE post_id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, postId);

            ps.executeUpdate();
        }
    }

}