package com.gow.pulsar.manager.gen.dao;

import com.gow.pulsar.manager.gen.model.Tokens;
import com.gow.pulsar.manager.gen.model.TokensExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TokensMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    long countByExample(TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int deleteByExample(TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long tokenId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int insert(Tokens record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int insertSelective(Tokens record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    Tokens selectOneByExample(TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    List<Tokens> selectByExample(TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    Tokens selectByPrimaryKey(Long tokenId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Tokens record, @Param("example") TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Tokens record, @Param("example") TokensExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Tokens record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Tokens record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int batchInsert(@Param("list") List<Tokens> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tokens
     *
     * @mbg.generated
     */
    int batchInsertSelective(@Param("list") List<Tokens> list, @Param("selective") Tokens.Column ... selective);
}